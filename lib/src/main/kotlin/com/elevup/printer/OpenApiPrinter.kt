package com.elevup.printer

import com.elevup.generator.CachedGenerator
import com.elevup.model.INDENT

class OpenApiPrinter(
    private val indent: String = INDENT
) : Printer {

    override fun printString(generator: CachedGenerator): String = with(generator) {
        buildString {
            appendLine("schemas:")
            appendLine(
                generatedTypeAliases
                    .sortedBy { it.name }
                    .joinToString(separator = "\n\n") { it.code }
                    .prependIndent(indent)
            )
            appendLine(
                generatedEnums
                    .sortedBy { it.name }
                    .joinToString(separator = "\n\n") { it.code }
                    .prependIndent(indent)
            )
            appendLine(
                generatedClasses
                    .sortedBy { it.name }
                    .joinToString(separator = "\n\n") { it.code }
                    .prependIndent(indent)
            )
        }
    }
}
