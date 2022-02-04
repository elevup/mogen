package com.elevup.languages.swift

import com.elevup.ClassComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.model.Type
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class SwiftClassComposer : ClassComposer {
    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("struct $typeName: Codable {")
    }

    override fun StringBuilder.appendProperty(
        name: String,
        type: Type,
        formatType: (Type) -> String,
        annotations: MergedAnnotations,
        indent: String?
    ) {
        val realName =  annotations.fieldName ?: name

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
            appendLine("@available(*, deprecated, message: \"${annotations.deprecated}\")", indent)
        }

        appendLine("let $realName: ${formatType(type)}", indent)
    }

    override fun StringBuilder.appendFooter() {
        appendLine("}")
    }
}
