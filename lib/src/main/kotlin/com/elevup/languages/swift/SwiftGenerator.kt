package com.elevup.languages.swift

import com.elevup.annotation.AnnotationProcessor
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.generator.CachedGenerator
import com.elevup.model.*
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.jvm.jvmName

class SwiftGenerator(
    override val annotationProcessors: List<AnnotationProcessor> = emptyList(),
    private val config: ComposerConfig = ComposerConfig(),
    indents: Indents = GenericIndents(),
) : CachedGenerator(
    annotationProcessors,
    indents,
    SwiftClassComposer(config = config),
    SwiftEnumComposer(config = config),
    SwiftTypealiasComposer(config = config)
) {


    override fun Type.format(annotations: MergedAnnotations): String = when (this) {
        is Type.Iterable -> {
            val subType = type?.localType ?: Type.Any
            if (nullable) {
                "[${subType.format()}]?"
            } else {
                "[${subType.format()}]"
            }
        }
        is Type.Primitive -> {
            if (nullable) {
                "$name?"
            } else {
                name
            }
        }
        is Type.Reference -> if (nullable) {
            "${config.formatName(name)}?"
        } else {
            config.formatName(name)
        }
        Type.Any -> "Any"
    }


    override val KClassifier.primitiveType
        get() = when (this) {
            Boolean::class -> "Bool"
            String::class, Char::class -> "String"
            Int::class,
            Long::class,
            Short::class,
            Byte::class -> "Int"
            Float::class -> "Float"
            Double::class -> "Double"
            else -> null
        }


    override val KClass<*>.generatedName: String
        get() = jvmName.split(".").last().replace("$", "")

}
