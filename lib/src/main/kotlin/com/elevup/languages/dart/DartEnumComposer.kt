package com.elevup.languages.dart

import com.elevup.EnumComposer
import com.elevup.util.appendLine
import kotlin.reflect.KClass

class DartEnumComposer : EnumComposer {

    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("enum $typeName {")
    }

    override fun StringBuilder.appendProperties(klass: KClass<*>, klassNames: Map<Any, String>, indent: String?) {
        klass.java.enumConstants.map { constant: Any ->
            val fieldName = klassNames[constant] ?: constant
            "$constant, // = $fieldName"
        }.forEach {
            appendLine(it, indent)
        }
    }

    override fun StringBuilder.appendFooter() {
        append("}")
    }
}
