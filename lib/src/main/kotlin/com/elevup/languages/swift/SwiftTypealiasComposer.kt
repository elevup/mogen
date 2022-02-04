package com.elevup.languages.swift

import com.elevup.TypealiasComposer
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class SwiftTypealiasComposer : TypealiasComposer {
    override fun StringBuilder.appendTypealias(aliasName: String, originalName: String, comment: String?, indent: String?) {
        comment?.let { appendLine(it.wrapIntoComment()) }
        appendLine("typealias $aliasName = $originalName", indent)
    }
}
