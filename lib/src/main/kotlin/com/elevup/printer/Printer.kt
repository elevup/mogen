package com.elevup.printer

import com.elevup.generator.CachedGenerator

/**
 * Converts all cached definitions to string
 */
interface Printer {

    /**
     * Iterates through all generated definitions and returns one string
     * that contains all definitions
     */
    fun printString(generator: CachedGenerator): String

}
