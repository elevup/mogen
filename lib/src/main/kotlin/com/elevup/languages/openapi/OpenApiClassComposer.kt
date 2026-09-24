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

    override fun StringBuilder.appendHeader(
        typeName: String,
        sealedSuperclass: String?,
        sealedSubclasses: List<String>
    ) {
        appendLine("${config.formatName(typeName)}:")
        if (sealedSubclasses.isNotEmpty()) {
            appendLine("  allOf:")
            appendLine("    - type: object")
            appendLine("      properties:")
        } else {
            appendLine("  type: object")
            appendLine("  properties:")
        }
    }

    override fun StringBuilder.appendProperty(
        name: String,
        type: Type,
        formatType: (Type) -> String,
        annotations: MergedAnnotations,
        indent: String?,
        isOverride: Boolean,
        isSealedSuperclass: Boolean
    ) {
        val propertyCode = buildString {
            val realName = annotations.fieldName ?: name

            // Omitted property is expressed by not being required, value itself is described by the inner type
            val type = (type as? Type.Optional)?.type ?: type

            if (type is Type.Any) {
                appendLine("$realName: {}")
            } else {
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
        }

        appendLine(propertyCode.trimEnd().let { if (isSealedSuperclass) it.prependIndent("    ") else it })

    }

    override fun StringBuilder.appendFooter(
        sealedSuperclass: String?,
        sealedSubclasses: List<String>
    ) {
        // Class without properties (e.g. object in sealed hierarchy) -> `properties: {}`
        if (endsWith("properties:\n")) {
            setLength(length - 1)
            appendLine(" {}")
        }

        if (sealedSubclasses.isNotEmpty()) {
            appendLine("    - oneOf:")
            sealedSubclasses.forEach {
                appendLine("      - \$ref: '#/components/schemas/${config.formatName(it)}'")
            }
        }
    }
}
