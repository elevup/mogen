package com.elevup.generator

import com.elevup.ClassComposer
import com.elevup.ConstructorComposer
import com.elevup.EnumComposer
import com.elevup.TypealiasComposer
import com.elevup.annotation.AnnotationProcessor
import com.elevup.model.GeneratedType
import com.elevup.model.Indents
import com.elevup.model.Type
import com.elevup.model.Typealias
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Does the same job as [Generator] but holds cache of already visited types and generated definitions.
 * Feed this lil fella with types, and it will continuously generate new definitions.
 *
 * Feed it using [appendClasses], [appendTypeAliases]
 * Postprocess them using [generatedClasses], [generatedEnums] and [generatedTypeAliases] properties
 */
abstract class CachedGenerator(
    override val annotationProcessors: List<AnnotationProcessor>,
    indents: Indents,
    classGenerator: ClassComposer,
    enumGenerator: EnumComposer,
    typealiasGenerator: TypealiasComposer,
    constructorGenerator: ConstructorComposer? = null,
) : Generator(annotationProcessors, indents, classGenerator, enumGenerator, typealiasGenerator, constructorGenerator) {
    private val visitedClasses: MutableSet<KClass<*>> = mutableSetOf()
    private val visitedTypes: MutableSet<String> = mutableSetOf()

    private val _generatedClasses: MutableList<GeneratedType> = mutableListOf()
    private val _generatedEnums: MutableList<GeneratedType> = mutableListOf()
    private val _generatedTypeAliases: MutableList<GeneratedType> = mutableListOf()

    val generatedClasses: List<GeneratedType> = _generatedClasses
    val generatedEnums: List<GeneratedType> = _generatedEnums
    val generatedTypeAliases: List<GeneratedType> = _generatedTypeAliases


    /**
     * Generates code for given classes
     */
    fun appendClasses(klasses: List<KClass<*>>): CachedGenerator {
        klasses.forEach { appendClass(it) }
        return this
    }

    /**
     * Generates code for given types
     */
    fun appendTypeAliases(aliases: List<Typealias>): CachedGenerator {
        aliases.forEach { appendTypeAlias(it) }
        return this
    }

    /**
     * Generates code for given class
     */
    fun appendClass(klass: KClass<*>) : CachedGenerator {
        onClass(klass)
        return this
    }

    /**
     * Generates code for given type
     */
    fun appendTypeAlias(alias: Typealias): CachedGenerator {
        appendTypeAliasInternal(alias)
        return this
    }

    override fun onClass(klass: KClass<*>) {
        if (!visitedClasses.contains(klass)) {
            visitedClasses += klass

            if (klass.java.isEnum) {
                visitEnum(klass)
            } else {
                visitClass(klass)
            }
        }
    }

    private fun visitClass(klass: KClass<*>) {
        generateClass(klass)?.let { definition ->
            _generatedClasses += GeneratedType(
                name = klass.generatedName,
                code = definition,
            )
        }

    }

    private fun visitEnum(klass: KClass<*>) {
        _generatedEnums += GeneratedType(
            name = klass.generatedName,
            code = generateEnum(klass)
        )
    }

    override fun onTypealias(replacement: String, original: KType) {
        val originalName = (original.classifier?.createType(original.arguments)?.localType ?: Type.Any).format()
        appendTypeAliasInternal(replacement, originalName)
    }

    private fun appendTypeAliasInternal(alias: Typealias) {
        // Custom-made types cannot be nullable
        val originalName = Type.Primitive(alias.name, nullable = false).format()
        appendTypeAliasInternal(alias.localClass.generatedName, originalName, alias.comment)
    }

    private fun appendTypeAliasInternal(
        aliasName: String,
        originalName: String,
        comment: String? = null
    ) {
        if (!visitedTypes.contains(aliasName)) {
            visitedTypes += aliasName

            GeneratedType(
                name = aliasName,
                code = generateTypealias(aliasName, originalName, comment)
            ).let { _generatedTypeAliases += it }
        }
    }

}
