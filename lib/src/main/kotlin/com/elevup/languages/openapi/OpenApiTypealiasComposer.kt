package com.elevup.languages.openapi

import com.elevup.TypealiasComposer
import com.elevup.model.ComposerConfig
import com.elevup.model.formatName
import com.elevup.util.appendLine

class OpenApiTypealiasComposer(
    override val config: ComposerConfig
) : TypealiasComposer {

    override fun StringBuilder.appendTypealias(aliasName: String, originalName: String, comment: String?, indent: String?) {
        appendLine("${config.formatName(aliasName)}:")
        appendLine(originalName, indent)
    }
}
