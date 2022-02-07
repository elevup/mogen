package com.elevup.languages.ts

import com.elevup.EnumComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.languages.typeScriptDeprecated
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment
import kotlin.reflect.KClass

class TypescriptEnumComposer : EnumComposer {

    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("export enum $typeName {")
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
