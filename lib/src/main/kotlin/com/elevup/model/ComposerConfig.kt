package com.elevup.model


/**
 * Generic config that is accepted by all composers and modifies the output
 */
data class ComposerConfig(
    /**
     * All generated types names will start with this character sequence
     */
    val typeNamePrefix: String = "",
    /**
     * All generated types names will end with this character sequence
     */
    val typeNamePostfix: String = "",
)

/**
 * Glues prefix + postfix to [suggestedName]
 */
fun ComposerConfig.formatName(suggestedName: String) = buildString {
    append(typeNamePrefix)
    append(suggestedName)
    append(typeNamePostfix)
}

