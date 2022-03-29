package com.elevup.languages.swift

import com.elevup.TypealiasComposer
import com.elevup.model.ComposerConfig
import com.elevup.model.formatName
import com.elevup.util.appendLine
import com.elevup.util.wrapIntoComment

class SwiftTypealiasComposer(
    override val config: ComposerConfig
) : TypealiasComposer {

    override fun StringBuilder.appendTypealias(aliasName: String, originalName: String, comment: String?, indent: String?) {
        comment?.let { appendLine(it.wrapIntoComment()) }
        appendLine("typealias ${config.formatName(aliasName)} = $originalName", indent)
    }
}
