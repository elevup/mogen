package com.elevup.model


/**
 * Represents strings that will be prepended to every line when generating definitions
 */
data class Indents(
    val classProperties: String,
    val classProperty: String,
    val enumProperty: String,
    val typeAlias: String,
    val constructor: String,
    val constructorProperty: String
)

/**
 * Default indent
 */
const val INDENT = "  "

/**
 * Default indents for OpenApi
 */
fun OpenApiIndents(indent: String = INDENT) = Indents(
    classProperties = indent + indent,
    classProperty = indent,
    enumProperty = indent,
    typeAlias = indent,
    constructor = "",
    constructorProperty = ""
)

/**
 * Default indents that should work for most of the programming languages
 */
fun GenericIndents(indent: String = INDENT) = Indents(
    classProperties = "",
    classProperty = indent,
    enumProperty = indent,
    typeAlias = "",
    constructor = indent,
    constructorProperty = indent + indent
)
