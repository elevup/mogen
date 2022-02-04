package com.elevup.languages.ts

import com.elevup.TypealiasComposer
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class TypescriptTypealiasComposer : TypealiasComposer {
    override fun StringBuilder.appendTypealias(aliasName: String, originalName: String, comment: String?, indent: String?) {
        comment?.let { appendLine(it.wrapIntoComment()) }
        appendLine("export type $aliasName = $originalName", indent)
    }
}
