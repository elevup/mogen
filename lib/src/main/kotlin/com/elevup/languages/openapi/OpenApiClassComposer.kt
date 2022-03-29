package com.elevup.languages.openapi

import com.elevup.ClassComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.model.ComposerConfig
import com.elevup.model.Type
import com.elevup.model.formatName
import com.elevup.util.appendLine

class OpenApiClassComposer(
    override val config: ComposerConfig
) : ClassComposer {

    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("${config.formatName(typeName)}:")
        appendLine("  type: object")
        appendLine("  properties:")
    }

    override fun StringBuilder.appendProperty(
        name: String,
        type: Type,
        formatType: (Type) -> String,
        annotations: MergedAnnotations,
        indent: String?
    ) {
        val realName = annotations.fieldName ?: name

        appendLine("$realName:")
        appendLine(formatType(type), indent)

        if (type is Type.Primitive) {
            when (type.name) {
                "number" -> {
                    annotations.min?.let { appendLine("minimum: $it", indent) }
                    annotations.max?.let { appendLine("maximum: $it", indent) }
                }
                "string" -> {
                    annotations.min?.let { appendLine("minLength: $it", indent) }
                    annotations.max?.let { appendLine("maxLength: $it", indent) }
                    annotations.regex?.let { appendLine("pattern: '$it'", indent) }
                }
            }
        }

    }

    override fun StringBuilder.appendFooter() {

    }
}
