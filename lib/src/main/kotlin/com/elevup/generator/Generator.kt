package com.elevup.generator

import com.elevup.ClassComposer
import com.elevup.ConstructorComposer
import com.elevup.EnumComposer
import com.elevup.TypealiasComposer
import com.elevup.annotation.AnnotationProcessor
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.annotation.model.merge
import com.elevup.generator.annotation.GeneratorIgnore
import com.elevup.model.Indents
import com.elevup.model.Type
import com.elevup.model.Typealias
import com.elevup.util.appendLine
import com.elevup.util.exportableMemberProperties
import com.elevup.util.getAnnotations
import com.elevup.util.safeObjectInstance
import kotlin.reflect.*
import kotlin.reflect.full.*

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
                if (classifier == Any::class) {
                    Type.Any
                } else if (classifier.isSubclassOf(Iterable::class) || classifier.javaObjectType.isArray) {
                    val itemType = when (classifier) {
                        IntArray::class -> Int::class.createType(nullable = false)
                        ShortArray::class -> Short::class.createType(nullable = false)
                        ByteArray::class -> Byte::class.createType(nullable = false)
                        CharArray::class -> Char::class.createType(nullable = false)
                        LongArray::class -> Long::class.createType(nullable = false)
                        FloatArray::class -> Float::class.createType(nullable = false)
                        DoubleArray::class -> Double::class.createType(nullable = false)

                        // Class container types (they use generics)
                        else -> arguments.singleOrNull()?.type
                    }

                    if (itemType != null) {
                        Type.Iterable(itemType, nullable = isMarkedNullable)
                    } else if (arguments.isEmpty()) {
                        // Iterable without arguments
                        onClass(klass = classifier)
                        Type.Reference(classifier.generatedName, nullable = isMarkedNullable)
                    } else {
                        // TODO Support iterables with multiple arguments
                        Type.Any
                    }
                } else if (classifier.isSubclassOf(Map::class)) {
                    val keyType = arguments.getOrNull(0)?.type
                    // in JSON only maps with String keys are allowed
                    if (keyType?.isSubtypeOf(typeOf<CharSequence>()) == true) {
                        Type.Map(
                            valueType = arguments.getOrNull(1)?.type,
                            nullable = isMarkedNullable
                        )
                    } else {
                        Type.Any
                    }
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
    protected fun generateClass(klass: KClass<*>): String? {
        if (klass.annotations.any { it is GeneratorIgnore }) return null
        if (klass.safeObjectInstance != null && klass.sealedSuperclass == null) return null

        return with(classGenerator) {
            val rawSealedSuperclass = klass.sealedSuperclass
            val rawSealedSubclasses = klass.exportableSealedSubclasses
            val isSealedSuperclass = rawSealedSubclasses.isNotEmpty()

            // class properties
            val rawProperties = klass.exportableMemberProperties.mapNotNull { property ->
                property.toTempProperty(
                    klass = klass,
                    isOverride = rawSealedSuperclass?.memberProperties
                        ?.any { it.name == property.name && (it.isOpen || it.isAbstract) } == true
                )
            }

            // Non-overridable properties inherited from sealed parent. They are not declared by this class,
            // but some languages have to repeat them (OpenApi, Swift) or pass them to parent's constructor (Dart).
            val inheritedProperties = rawSealedSuperclass?.exportableMemberProperties.orEmpty()
                .filter { parentProperty -> rawProperties.none { it.property.name == parentProperty.name } }
                .mapNotNull { it.toTempProperty(klass = rawSealedSuperclass!!, isOverride = true) }

            // No properties to export -> class can be skipped unless it is a part of sealed hierarchy
            if (rawProperties.isEmpty() && rawSealedSuperclass == null && !isSealedSuperclass) {
                return@with null
            }
            val allProperties = (rawProperties + inheritedProperties).sortedBy { it.property.name }

            val sealedSuperclass = rawSealedSuperclass?.generatedName
            val sealedSubclasses = rawSealedSubclasses.map { it.generatedName }

            buildString {
                appendHeader(klass.generatedName, sealedSuperclass, sealedSubclasses)
                if (allProperties.isNotEmpty()) {
                    appendLine(buildString {
                        allProperties.forEach { (annotations, type, property, isOverride) ->
                            appendProperty(
                                name = property.name,
                                type = type,
                                formatType = { type.format(annotations) },
                                annotations = annotations,
                                indent = indents.classProperty,
                                isOverride = isOverride,
                                isSealedSuperclass = isSealedSuperclass
                            )
                        }
                    }.trimEnd(), indents.classProperties)
                }

                // Optionally append constructor if required by language
                constructorGenerator
                    ?.takeIf { !isSealedSuperclass || it.useForSealedSuperclasses() }
                    ?.apply {
                        appendLine()
                        appendHeader(klass.generatedName, indents.constructor)

                        allProperties.forEach { (annotations, type, property, isOverride) ->
                            appendProperty(
                                name = property.name,
                                type = type,
                                formatType = { type.format(annotations) },
                                annotations = annotations,
                                indent = indents.constructorProperty,
                                isOverride = isOverride,
                                isSealedSuperclass = isSealedSuperclass
                            )
                        }

                        appendFooter(indents.constructor)
                    }

                appendFooter(sealedSuperclass, sealedSubclasses)
            }.trim()
        }
    }

    /**
     * Classes of the same sealed hierarchy (sealed parent and sealed subclasses) that has to be generated
     * together with [this] class, otherwise the output would reference missing types.
     */
    protected val KClass<*>.sealedRelatives: List<KClass<*>>
        get() = listOfNotNull(sealedSuperclass) + exportableSealedSubclasses

    private val KClass<*>.sealedSuperclass: KClass<*>?
        get() {
            val rawSealedSuperclasses = superclasses.filter { it.isSealed }
            if (rawSealedSuperclasses.size > 1 || (rawSealedSuperclasses.isNotEmpty() && isSealed)) {
                throw IllegalStateException("Only one sealed parent is allowed")
            }
            return rawSealedSuperclasses.firstOrNull()
        }

    private val KClass<*>.exportableSealedSubclasses: List<KClass<*>>
        get() = sealedSubclasses.filter { subclass -> subclass.annotations.none { it is GeneratorIgnore } }

    private fun KProperty1<*, *>.toTempProperty(klass: KClass<*>, isOverride: Boolean): TempProperty? {
        val annotations = getAnnotations(klass)
        if (annotations.any { it is GeneratorIgnore }) return null

        return TempProperty(
            annotations = annotationProcessors.map { it.process(annotations, klass) }.flatten().merge(),
            localType = returnType.localType,
            property = this,
            isOverride = isOverride
        )
    }

    /**
     * Generates enum definition from [klass] using [enumGenerator]
     */
    protected fun generateEnum(klass: KClass<*>) = with(enumGenerator) {
        buildString {
            appendHeader(klass.generatedName)
            klass.java.enumConstants.forEach {
                val annotations = (it as Enum<*>).getAnnotations(klass)
                if (annotations.none { it is GeneratorIgnore }) {
                    appendProperty(
                        name = it.toString(),
                        annotations = annotationProcessors.map { it.process(annotations, klass) }.flatten().merge(it),
                        indent = indents.enumProperty,
                    )
                }
            }
            appendFooter()
        }.trim()
    }

    /**
     * Generates typealias definition for target language using [typealiasGenerator]
     */
    protected fun generateTypealias(alias: Typealias) =
        generateTypealias(
            aliasName = alias.localClass.generatedName,
            originalName = alias.name,
            comment = alias.comment,
        )

    /**
     * Generates typealias definition for target language using [typealiasGenerator]
     */
    protected fun generateTypealias(
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
            if (it.groups.size == 5) {
                val aliasName = it.groups[1]!!.value.split(".").last()
                aliasName
            } else null
        }


    private data class TempProperty(
        val annotations: MergedAnnotations,
        val localType: Type,
        val property: KProperty1<*, *>,
        val isOverride: Boolean
    )

    companion object {
        /**
         * Regex that matches trivial Typealiases
         * Example: `com.elevup.UserId /* = kotlin.String */`
         */
        private val TYPEALIAS_REGEX = "^([a-zA-Z0-9.]+)(\\?)? \\/\\* = ([a-zA-Z0-9<>.]+)(\\?)? \\*\\/".toRegex()
    }
}
