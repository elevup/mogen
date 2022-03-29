package com.elevup.languages.openapi

import com.elevup.annotation.AnnotationProcessor
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.generator.CachedGenerator
import com.elevup.model.*
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmName

class OpenApiGenerator(
    override val annotationProcessors: List<AnnotationProcessor> = emptyList(),
    private val config: ComposerConfig = ComposerConfig(),
    indents: Indents = OpenApiIndents(),
) : CachedGenerator(
    annotationProcessors,
    indents,
    OpenApiClassComposer(config = config),
    OpenApiEnumComposer(config = config),
    OpenApiTypealiasComposer(config = config),
) {

    override val KClass<*>.generatedName
        get() = jvmName.split(".").last().replace("$", "")

    override fun Type.format(annotations: MergedAnnotations): String = buildString {
        when (this@format) {
            Type.Any -> {
                appendLine("type: object")
                appendLine("additionalProperties: true")
                if (annotations.deprecated != null) {
                    appendLine("deprecated: true")
                    appendLine("description: DEPRECATED ~ ${annotations.deprecated}")
                }
            }
            is Type.Iterable -> {
                appendLine("type: array")
                if (annotations.deprecated != null) {
                    appendLine("deprecated: true")
                    appendLine("description: DEPRECATED ~ ${annotations.deprecated}")
                }
                appendLine("nullable: $nullable")
                appendLine("items:")
                val subType = type?.localType ?: Type.Any
                appendLine(subType.format(MergedAnnotations()).prependIndent(INDENT))
            }
            is Type.Primitive -> {
                appendLine("type: $name")
                type?.openApiFormat?.let { appendLine("format: $it") }
                appendLine("nullable: $nullable")
                if (annotations.deprecated != null) {
                    appendLine("deprecated: true")
                    appendLine("description: DEPRECATED ~ ${annotations.deprecated}")
                }
            }
            is Type.Reference -> {
                if (nullable) {
                    appendLine("nullable: true")
                }

                if (annotations.deprecated == null) {
                    if (nullable) {
                        appendLine("allOf:")
                        appendLine(" - \$ref: '#/components/schemas/${config.formatName(name)}'")
                    } else {
                        appendLine("\$ref: '#/components/schemas/${config.formatName(name)}'")
                    }
                } else {
                    appendLine("allOf:")
                    appendLine(" - \$ref: '#/components/schemas/${config.formatName(name)}'")
                    appendLine(" - deprecated: true")
                    appendLine(" - description: DEPRECATED ~ ${annotations.deprecated}")
                }
            }
        }
    }.removeSuffix("\n")


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

    private val KType.openApiFormat: String?
        get() = when (classifier) {
            Int::class -> "int32"
            Long::class -> "int64"
            Byte::class -> "number"
            Float::class -> "float"
            Double::class -> "double"
            else -> null
        }

    companion object {
        private const val INDENT = "  "
    }

}
