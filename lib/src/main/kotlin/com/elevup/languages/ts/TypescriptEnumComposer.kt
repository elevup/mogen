package com.elevup.languages.ts

import com.elevup.EnumComposer
import com.elevup.util.appendLine
import kotlin.reflect.KClass

class TypescriptEnumComposer : EnumComposer {

    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("export enum $typeName {")
    }

    override fun StringBuilder.appendProperties(klass: KClass<*>, klassNames: Map<Any, String>, indent: String?) {
        klass.java.enumConstants.map { constant ->
            val fieldName = klassNames[constant] ?: constant
            "$constant = '$fieldName',"
        }.forEach {
            appendLine(it, indent)
        }
    }

    override fun StringBuilder.appendFooter() {
        append("}")
    }
}
