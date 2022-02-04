package com.elevup.languages.swift

import com.elevup.EnumComposer
import com.elevup.util.appendLine
import com.elevup.util.snakeToLowerCamelCase
import kotlin.reflect.KClass

class SwiftEnumComposer : EnumComposer {

    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("enum ${typeName}: String, Codable {")
    }

    override fun StringBuilder.appendProperties(
        klass: KClass<*>,
        klassNames: Map<Any, String>,
        indent: String?
    ) {
        klass.java.enumConstants.map { constant ->
            val name = constant.toString().snakeToLowerCamelCase()
            val fieldName = klassNames[constant] ?: constant
            "case $name = \"$fieldName\""
        }.forEach {
            appendLine(it, indent)
        }

    }

    override fun StringBuilder.appendFooter() {
        append("}")
    }

}
