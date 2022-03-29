package com.elevup.languages.ts

import com.elevup.EnumComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.languages.typeScriptDeprecated
import com.elevup.model.ComposerConfig
import com.elevup.model.formatName
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class TypescriptEnumComposer(
    override val config: ComposerConfig
) : EnumComposer {

    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("export enum ${config.formatName(typeName)} {")
    }

    override fun StringBuilder.appendProperty(name: String, annotations: MergedAnnotations, indent: String?) {
        val realName = annotations.fieldName ?: name
        if (annotations.deprecated != null) {
            appendLine(annotations.deprecated.typeScriptDeprecated().wrapIntoComment(), indent)
        }
        appendLine("$name = '$realName',", indent)
    }

    override fun StringBuilder.appendFooter() {
        append("}")
    }
}
