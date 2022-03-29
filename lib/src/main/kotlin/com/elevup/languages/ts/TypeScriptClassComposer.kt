package com.elevup.languages.ts

import com.elevup.ClassComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.languages.typeScriptDeprecated
import com.elevup.model.ComposerConfig
import com.elevup.model.Type
import com.elevup.model.formatName
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class TypeScriptClassComposer(
    override val config: ComposerConfig
) : ClassComposer {

    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("export interface ${config.formatName(typeName)} {")
    }

    override fun StringBuilder.appendProperty(
        name: String,
        type: Type,
        formatType: (Type) -> String,
        annotations: MergedAnnotations,
        indent: String?
    ) {
        val tempName = annotations.fieldName ?: name
        val realName = if (type.nullable) {
            "$tempName?"
        } else {
            tempName
        }

        listOfNotNull(
            "min: ${annotations.min}".takeIf { annotations.min != null },
            "max: ${annotations.max}".takeIf { annotations.max != null },
            "regex: ${annotations.regex}".takeIf { annotations.regex != null },
            annotations.deprecated?.typeScriptDeprecated()
        ).joinToString(separator = "\n")
            .takeIf { it.isNotBlank() }
            ?.wrapIntoComment()
            ?.also { comments ->
                appendLine(comments, indent)
            }

        appendLine("$realName: ${formatType(type)};", indent)
    }

    override fun StringBuilder.appendFooter() {
        appendLine("}")
    }
}
