package com.elevup.languages.dart

import com.elevup.TypealiasComposer
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class DartTypealiasComposer : TypealiasComposer {
    override fun StringBuilder.appendTypealias(aliasName: String, originalName: String, comment: String?, indent: String?) {
        comment?.let { appendLine(it.wrapIntoComment()) }
        appendLine("typedef $aliasName = $originalName", indent)
    }
}
