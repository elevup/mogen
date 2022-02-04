package com.elevup.util

/**
 * Appends a line and optionally an indent
 */
fun StringBuilder.appendLine(line: String, indent: String?) {
    if (indent == null) {
        appendLine(line)
    } else {
        appendLine(line.prependIndent(indent))
    }
}
