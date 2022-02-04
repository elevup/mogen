package com.elevup.languages.openapi

import com.elevup.TypealiasComposer
import com.elevup.util.appendLine

class OpenApiTypealiasComposer : TypealiasComposer {
    override fun StringBuilder.appendTypealias(aliasName: String, originalName: String, comment: String?, indent: String?) {
        appendLine("$aliasName:")
        appendLine(originalName, indent)
    }
}
