package com.elevup.generator

import com.elevup.printer.Printer

/**
 * Nicer way to convert cached output to String
 */
fun CachedGenerator.print(printer: Printer) = printer.printString(this)
