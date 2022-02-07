package com.elevup.languages.swift

import com.elevup.EnumComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.languages.swiftDeprecated
import com.elevup.util.appendLine
import com.elevup.util.snakeToLowerCamelCase

class SwiftEnumComposer : EnumComposer {

    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("enum ${typeName}: String, Codable {")
    }

    override fun StringBuilder.appendProperty(name: String, annotations: MergedAnnotations, indent: String?) {
        val realName = name.snakeToLowerCamelCase()
        if (annotations.deprecated != null) {
            appendLine(annotations.deprecated.swiftDeprecated(), indent)
        }
        appendLine("case $realName = \"${annotations.fieldName ?: name}\"", indent)
    }

    override fun StringBuilder.appendFooter() {
        append("}")
    }

}
