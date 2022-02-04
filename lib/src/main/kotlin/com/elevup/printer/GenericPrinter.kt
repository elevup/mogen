package com.elevup.printer

import com.elevup.generator.CachedGenerator
import com.elevup.util.wrapIntoComment

object GenericPrinter : Printer {

    override fun printString(generator: CachedGenerator) = with(generator) {
        buildString {
            if (generatedTypeAliases.isNotEmpty()) {
                appendLine("TYPES".wrapIntoComment())
                appendLine(generatedTypeAliases.sortedBy { it.name }.joinToString(separator = "\n") { it.code }.trim())
            }

            if (generatedEnums.isNotEmpty()) {
                if (isNotEmpty()) appendLine()
                appendLine("ENUMS".wrapIntoComment())
                appendLine(generatedEnums.sortedBy { it.name }.joinToString(separator = "\n\n") { it.code }.trim())
            }

            if (generatedClasses.isNotEmpty()) {
                if (isNotEmpty()) appendLine()
                appendLine("MODELS".wrapIntoComment())
                appendLine(generatedClasses.sortedBy { it.name }.joinToString(separator = "\n\n") { it.code }.trim())
            }
        }
    }

}
