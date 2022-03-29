package com.elevup.languages.swift

import com.elevup.EnumComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.languages.swiftDeprecated
import com.elevup.model.ComposerConfig
import com.elevup.model.formatName
import com.elevup.util.appendLine
import com.elevup.util.snakeToLowerCamelCase

class SwiftEnumComposer(
    override val config: ComposerConfig
) : EnumComposer {

    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("enum ${config.formatName(typeName)}: String, Codable {")
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
