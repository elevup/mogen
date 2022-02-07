package com.elevup.languages

/**
 * Generates string representing that something in deprecated in Swift
 */
fun String.swiftDeprecated() =
    "@available(*, deprecated, message: \"${replace("\"", "\\\"")}\")"

/**
 * Generates string representing that something in deprecated in TypeScript
 */
fun String.typeScriptDeprecated() =
    "@deprecated ${replace("*/", "*\\/")}"

/**
 * Generates string representing that something in deprecated in Dart
 */
fun String.dartDeprecated() =
    "@deprecated ${replace("*/", "*\\/")}"
