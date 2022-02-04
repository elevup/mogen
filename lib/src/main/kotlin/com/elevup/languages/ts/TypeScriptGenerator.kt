package com.elevup.languages.ts

import com.elevup.annotation.AnnotationProcessor
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.generator.CachedGenerator
import com.elevup.model.GenericIndents
import com.elevup.model.Indents
import com.elevup.model.Type
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.jvm.jvmName

class TypeScriptGenerator(
    override val annotationProcessors: List<AnnotationProcessor> = emptyList(),
    indents: Indents = GenericIndents(),
) : CachedGenerator(
    annotationProcessors,
    indents,
    TypeScriptClassComposer(),
    TypescriptEnumComposer(),
    TypescriptTypealiasComposer()
) {

    override fun Type.format(annotations: MergedAnnotations): String = when (this) {
        is Type.Iterable -> {
            val subType = type?.localType ?: Type.Any
            if (type?.isMarkedNullable == true) {
                "${subType.format()}[] | null"
            } else {
                "${subType.format()}[]"
            }
        }
        is Type.Primitive -> {
            if (nullable) {
                "$name | null"
            } else {
                name
            }
        }
        is Type.Reference -> if (nullable) {
            "$name | null"
        } else {
            name
        }
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
