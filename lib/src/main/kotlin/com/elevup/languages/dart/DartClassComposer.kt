package com.elevup.languages.dart

import com.elevup.ClassComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.model.Type
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class DartClassComposer : ClassComposer {
    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("class $typeName {")
    }

    override fun StringBuilder.appendProperty(
        name: String,
        type: Type,
        formatType: (Type) -> String,
        annotations: MergedAnnotations,
        indent: String?
    ) {
        val realName = annotations.fieldName ?: name
        listOfNotNull(
            "min: ${annotations.min}".takeIf { annotations.min != null },
            "max: ${annotations.max}".takeIf { annotations.max != null },
            "regex: ${annotations.regex}".takeIf { annotations.regex != null },
            "@deprecated ${annotations.deprecated}".takeIf { annotations.deprecated != null }
        ).joinToString(separator = "\n")
            .takeIf { it.isNotBlank() }
            ?.wrapIntoComment()
            ?.also { comments ->
                appendLine(comments, indent)
            }

        appendLine("final ${formatType(type)} $realName;", indent)
    }

    override fun StringBuilder.appendFooter() {
        appendLine("}")
    }
}
