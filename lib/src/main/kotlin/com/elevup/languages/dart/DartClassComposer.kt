package com.elevup.languages.dart

import com.elevup.ClassComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.languages.dartDeprecated
import com.elevup.model.ComposerConfig
import com.elevup.model.Type
import com.elevup.model.formatName
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class DartClassComposer(
    override val config: ComposerConfig
) : ClassComposer {

    override fun StringBuilder.appendHeader(
        typeName: String,
        sealedSuperclass: String?,
        sealedSubclasses: List<String>
    ) {
        if (sealedSubclasses.isNotEmpty()) {
            append("sealed class ${config.formatName(typeName)}")
        } else {
            append("class ${config.formatName(typeName)}")
        }

        sealedSuperclass?.also { append(" extends ${config.formatName(it)}") }

        appendLine(" {")
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
        if (isOverride) return

        val realName = annotations.fieldName ?: name
        listOfNotNull(
            "min: ${annotations.min}".takeIf { annotations.min != null },
            "max: ${annotations.max}".takeIf { annotations.max != null },
            "regex: ${annotations.regex}".takeIf { annotations.regex != null },
            type.optionalComment(),
            annotations.deprecated?.dartDeprecated()
        ).joinToString(separator = "\n")
            .takeIf { it.isNotBlank() }
            ?.wrapIntoComment()
            ?.also { comments ->
                appendLine(comments, indent)
            }

        appendLine("final ${formatType(type)} $realName;", indent)
    }

    private fun Type.optionalComment(): String? = when {
        this !is Type.Optional -> null
        type.nullable -> "optional: may be omitted, omitted and null are different states"
        else -> "optional: may be omitted"
    }

    override fun StringBuilder.appendFooter(
        sealedSuperclass: String?,
        sealedSubclasses: List<String>
    ) {
        appendLine("}")
    }
}
