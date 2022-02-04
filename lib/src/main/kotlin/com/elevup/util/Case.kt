package com.elevup.util

import java.util.*

val camelRegex = "(?<=[a-zA-Z])[A-Z]+".toRegex()
val snakeRegex = "_[a-zA-Z]+".toRegex()

fun String.camelToSnakeCase(): String = replace(camelRegex) {
    "_${it.value}".uppercase()
}

fun String.snakeToLowerCamelCase(): String = lowercase().replace(snakeRegex) {
    it.value.replace("_", "").capitalize()
}

fun String.snakeToUpperCamelCase(): String =
    snakeToLowerCamelCase().capitalize()

private fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase()) {
        it.titlecase(Locale.getDefault())
    } else {
        it.toString()
    }
}
