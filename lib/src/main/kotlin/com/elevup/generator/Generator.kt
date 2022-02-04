package com.elevup.generator

import com.elevup.ClassComposer
import com.elevup.ConstructorComposer
import com.elevup.EnumComposer
import com.elevup.TypealiasComposer
import com.elevup.annotation.AnnotationProcessor
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.annotation.model.merge
import com.elevup.model.Indents
import com.elevup.model.Type
import com.elevup.model.Typealias
import com.elevup.util.appendLine
import com.elevup.util.exportableMemberProperties
import com.elevup.util.getAnnotations
import com.elevup.util.safeObjectInstance
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf

abstract class Generator(
    open val annotationProcessors: List<AnnotationProcessor>,
    private val indents: Indents,
    private val classGenerator: ClassComposer,
    private val enumGenerator: EnumComposer,
    private val typealiasGenerator: TypealiasComposer,
    private val constructorGenerator: ConstructorComposer? = null,
) {

    /**
     * Converts Kotlin's type to local [Type] suitable for code generation
     */
    protected val KType.localType: Type
        get() {
            val alias = getTypealias()
            val classifier = classifier

            return if (alias != null) {
                onTypealias(alias, this)
                Type.Reference(alias, nullable = isMarkedNullable)
            } else if (classifier?.primitiveType != null) {
                Type.Primitive(classifier.primitiveType!!, this)
            } else if (classifier is KClass<*>) {
                if (classifier.isSubclassOf(Iterable::class) || classifier.javaObjectType.isArray) {
                    val itemType = when (classifier) {
                        IntArray::class -> Int::class.createType(nullable = false)
                        ShortArray::class -> Short::class.createType(nullable = false)
                        ByteArray::class -> Byte::class.createType(nullable = false)
                        CharArray::class -> Char::class.createType(nullable = false)
                        LongArray::class -> Long::class.createType(nullable = false)
                        FloatArray::class -> Float::class.createType(nullable = false)
                        DoubleArray::class -> Double::class.createType(nullable = false)

                        // Class container types (they use generics)
                        else -> arguments.single().type
                    }

                    Type.Iterable(itemType)
                } else if (classifier.isSubclassOf(Map::class)) {
                    // TODO Support maps
                    Type.Any
                } else {
                    onClass(klass = classifier)
                    Type.Reference(classifier.generatedName, nullable = isMarkedNullable)
                }
            } else {
                throw IllegalArgumentException("Cannot process '$this'")
            }
        }

    /**
     * To make codegen faster pass name of primitive type of output language
     * Example: Java's `Integer` maps to `Int` (Kotlin), `number` (TypeScript) etc.
     */
    protected open val KClassifier.primitiveType: String?
        get() = null


    /**
     * Print type as String
     */
    protected abstract fun Type.format(annotations: MergedAnnotations = MergedAnnotations()): String

    /**
     * Converts name of class to name suitable for output (language). Java tends to user '$' that are a bit tricky from time to time.
     * Example: Hello$World --> Hello.World
     */
    protected abstract val KClass<*>.generatedName: String

    /**
     * Typealias was discovered, up to if you want to process it.
     *
     * Assume example typealias declaration `typealias UserId = String`,
     * @param replacement - actual typealias name (UserId)
     * @param original - referenced type (String)
     */
    protected abstract fun onTypealias(replacement: String, original: KType)

    /**
     * Some class was discovered during scan of other class.
     * If you are willing to scan it call [generateClass] or [generateEnum] depending on its type.
     * You are advised to keep track of already discovered classes cause one class can be discovered multiple times,
     * maybe infinite times when it holds self as a nested child!
     */
    protected abstract fun onClass(klass: KClass<*>)

    /**
     * Generates class definition from [klass] using [enumGenerator]
     */
    fun generateClass(klass: KClass<*>): String? {
        // Object classes cannot be instantiated on the fly so they are gracefully ignored
        if (klass.safeObjectInstance != null) {
            return null
        }

        return with(classGenerator) {
            val rawProperties = klass.exportableMemberProperties.map { property ->
                TempProperty(
                    annotations = property.getAnnotations(klass).let { annotations ->
                        annotationProcessors.map { it.process(annotations, klass) }
                    }.flatten().merge(),
                    localType = property.returnType.localType,
                    property = property
                )
            }

            if (rawProperties.isNotEmpty()) {
                val properties = buildString {
                    rawProperties.forEach { (annotations, type, property) ->
                        appendProperty(
                            name = property.name,
                            type = type,
                            formatType = { type.format(annotations) },
                            annotations = annotations,
                            indent = indents.classProperty
                        )
                    }
                }

                buildString {
                    appendHeader(klass.generatedName)
                    appendLine(properties.trimEnd(), indents.classProperties)

                    // Optionally append constructor if required by language
                    constructorGenerator?.apply {
                        appendLine()
                        appendHeader(klass.generatedName, indents.constructor)

                        rawProperties.forEach { (annotations, type, property) ->
                            appendProperty(
                                name = property.name,
                                type = type,
                                formatType = { type.format(annotations) },
                                annotations = annotations,
                                indent = indents.constructorProperty
                            )
                        }

                        appendFooter(indents.constructor)
                    }

                    appendFooter()
                }.trim()
            } else {
                // No properties to export -> class can be skipped
                null
            }
        }
    }

    /**
     * Generates enum definition from [klass] using [enumGenerator]
     */
    fun generateEnum(klass: KClass<*>) = with(enumGenerator) {
        val fieldNames = annotationProcessors.map {
            it.process(klass.annotations, klass)
        }.flatten().merge().enumNames ?: emptyMap()

        buildString {
            appendHeader(klass.generatedName)
            appendProperties(klass, fieldNames, indents.enumProperty)
            appendFooter()
        }.trim()
    }

    /**
     * Generates typealias definition for target language using [typealiasGenerator]
     */
    fun generateTypealias(alias: Typealias) =
        generateTypealias(
            aliasName = alias.localClass.generatedName,
            originalName = alias.name,
            comment = alias.comment,
        )

    /**
     * Generates typealias definition for target language using [typealiasGenerator]
     */
    fun generateTypealias(
        aliasName: String,
        originalName: String,
        comment: String? = null,
    ) = with(typealiasGenerator) {
        buildString { appendTypealias(aliasName, originalName, comment, indents.typeAlias) }.trim()
    }


    /**
     * For given [KType] parses name of another type that it references
     *  - Example: `toString() == com.elevup.UserId /* = kotlin.String */`
     *  - Output: `UserId`
     */
    private fun KType.getTypealias(): String? =
        TYPEALIAS_REGEX.find(toString())?.let {
            if (it.groups.size == 3) {
                val aliasName = it.groups[1]!!.value.split(".").last()
                aliasName
            } else null
        }


    private data class TempProperty(
        val annotations: MergedAnnotations,
        val localType: Type,
        val property: KProperty1<*, *>,
    )

    companion object {
        /**
         * Regex that matches trivial Typealiases
         * Example: `com.elevup.UserId /* = kotlin.String */`
         */
        private val TYPEALIAS_REGEX = "^([a-zA-Z0-9.]+) \\/\\* = ([a-zA-Z0-9<>.]+) \\*\\/".toRegex()
    }
}
