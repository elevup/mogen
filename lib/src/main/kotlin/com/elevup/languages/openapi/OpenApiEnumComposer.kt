package com.elevup.languages.openapi

import com.elevup.EnumComposer
import com.elevup.util.appendLine
import kotlin.reflect.KClass

class OpenApiEnumComposer: EnumComposer {
    
    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("${typeName}:")
    }

    override fun StringBuilder.appendProperties(klass: KClass<*>, klassNames: Map<Any, String>, indent: String?) {
        val values = klass.java.enumConstants.map { constant ->
            klassNames[constant] ?: constant.toString()
        }.joinToString(", ") { it }
        
        appendLine("type: string", indent)
        appendLine("enum: [ $values ]", indent)
    }

    override fun StringBuilder.appendFooter() {
        
    }

}
