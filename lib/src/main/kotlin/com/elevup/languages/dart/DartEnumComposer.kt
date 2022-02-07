package com.elevup.languages.dart

import com.elevup.EnumComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.languages.dartDeprecated
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class DartEnumComposer : EnumComposer {

    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("enum $typeName {")
    }

    override fun StringBuilder.appendProperty(name: String, annotations: MergedAnnotations, indent: String?) {
        val realName = annotations.fieldName ?: name
        if (annotations.deprecated != null) {
            appendLine(annotations.deprecated.dartDeprecated().wrapIntoComment(), indent)
        }
        appendLine("$name, // = $realName", indent)
    }

    override fun StringBuilder.appendFooter() {
        append("}")
    }
}
