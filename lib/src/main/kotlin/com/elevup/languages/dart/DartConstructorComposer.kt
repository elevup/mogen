package com.elevup.languages.dart

import com.elevup.ConstructorComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.model.ComposerConfig
import com.elevup.model.Type
import com.elevup.model.formatName
import com.elevup.util.appendLine

class DartConstructorComposer(
    override val config: ComposerConfig
) : ConstructorComposer {

    override fun StringBuilder.appendHeader(typeName: String, indent: String?) {
        appendLine("${config.formatName(typeName)}({", indent)
    }

    override fun StringBuilder.appendProperty(
        name: String,
        type: Type,
        formatType: (Type) -> String,
        annotations: MergedAnnotations,
        indent: String?
    ) {
        val realName = annotations.fieldName ?: name
        appendLine("required this.$realName,", indent)

    }

    override fun StringBuilder.appendFooter(indent: String?) {
        appendLine("});", indent)
        appendLine()
    }
}
