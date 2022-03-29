package com.elevup.languages.ts

import com.elevup.annotation.AnnotationProcessor
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.generator.CachedGenerator
import com.elevup.model.*
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.jvm.jvmName

class TypeScriptGenerator(
    override val annotationProcessors: List<AnnotationProcessor> = emptyList(),
    private val config: ComposerConfig = ComposerConfig(),
    indents: Indents = GenericIndents(),
) : CachedGenerator(
    annotationProcessors,
    indents,
    TypeScriptClassComposer(config = config),
    TypescriptEnumComposer(config = config),
    TypescriptTypealiasComposer(config = config)
) {

    override fun Type.format(annotations: MergedAnnotations): String = when (this) {
        is Type.Iterable -> {
            val subType = type?.localType ?: Type.Any
            if (subType.nullable) {
                "(${subType.format()} | null)[]"
            } else {
                "${subType.format()}[]"
            }
        }
        is Type.Primitive -> name
        is Type.Reference -> config.formatName(name)
        Type.Any -> "any"
    }


    override val KClassifier.primitiveType
        get() = when (this) {
            Boolean::class -> "boolean"
            String::class, Char::class -> "string"
            Int::class,
            Long::class,
            Short::class,
            Byte::class -> "number"
            Float::class, Double::class -> "number"
            else -> null
        }


    override val KClass<*>.generatedName: String
        get() = jvmName.split(".").last().replace("$", "")

}
