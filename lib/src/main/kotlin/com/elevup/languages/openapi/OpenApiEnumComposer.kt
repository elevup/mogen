package com.elevup.languages.openapi

import com.elevup.EnumComposer
import com.elevup.annotation.model.MergedAnnotations

class OpenApiEnumComposer: EnumComposer {
    
    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("${typeName}:")
        appendLine("  type: string")
        append("  enum: [ ")
    }

    override fun StringBuilder.appendProperty(name: String, annotations: MergedAnnotations, indent: String?) {
        append(annotations.fieldName ?: name)
        append(',')
        append(' ')
    }

    override fun StringBuilder.appendFooter() {
        append("]")
    }

}
