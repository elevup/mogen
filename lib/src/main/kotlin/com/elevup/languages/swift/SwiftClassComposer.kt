package com.elevup.languages.swift

import com.elevup.ClassComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.languages.swiftDeprecated
import com.elevup.model.ComposerConfig
import com.elevup.model.Type
import com.elevup.model.formatName
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class SwiftClassComposer(
    override val config: ComposerConfig
) : ClassComposer {

    override fun StringBuilder.appendHeader(
        typeName: String,
        sealedSuperclass: String?,
        sealedSubclasses: List<String>
    ) {
        when {
            sealedSubclasses.isNotEmpty() -> appendLine("protocol ${config.formatName(typeName)} {")
            sealedSuperclass != null -> appendLine(
                "struct ${config.formatName(typeName)}: ${config.formatName(sealedSuperclass)}, Codable {"
            )

            else -> appendLine("struct ${config.formatName(typeName)}: Codable {")
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
        val realName = annotations.fieldName ?: name

        listOfNotNull(
            "min: ${annotations.min}".takeIf { annotations.min != null },
            "max: ${annotations.max}".takeIf { annotations.max != null },
            "regex: ${annotations.regex}".takeIf { annotations.regex != null },
        ).joinToString(separator = "\n")
            .takeIf { it.isNotBlank() }
            ?.wrapIntoComment()
            ?.also { comments ->
                appendLine(comments, indent)
            }

        if (annotations.deprecated != null) {
            appendLine(annotations.deprecated.swiftDeprecated(), indent)
        }

        if (isSealedSuperclass) {
            appendLine("var $realName: ${formatType(type)} { get }", indent)
        } else {
            appendLine("let $realName: ${formatType(type)}", indent)
        }
    }

    override fun StringBuilder.appendFooter(sealedSuperclass: String?, sealedSubclasses: List<String>) {
        appendLine("}")
    }
}
