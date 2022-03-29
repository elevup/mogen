package com.elevup.languages.openapi

import com.elevup.EnumComposer
import com.elevup.annotation.model.MergedAnnotations
import com.elevup.model.ComposerConfig
import com.elevup.model.formatName

class OpenApiEnumComposer(
    override val config: ComposerConfig
): EnumComposer {
    
    override fun StringBuilder.appendHeader(typeName: String) {
        appendLine("${config.formatName(typeName)}:")
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
