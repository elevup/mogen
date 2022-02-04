package com.elevup.annotation.model

/**
 * Represents supported annotations / constraints by this plugin
 */
sealed class ProcessedAnnotation {

    /**
     * Rename given field
     */
    data class FieldName(
        val name: String
    ) : ProcessedAnnotation()

    /**
     * Minimal value (numeric), minimal length (iterable / String)
     */
    data class Minimum(
        val min: Long
    ) : ProcessedAnnotation()

    /**
     * Maximal value (numeric), minimal length (iterable / String)
     */
    data class Maximum(
        val max: Long
    ) : ProcessedAnnotation()

    /**
     * Regex that must fit (String only)
     */
    data class Regex(
        val pattern: String
    ) : ProcessedAnnotation()

    /**
     * Field is deprecated and reason why
     */
    data class Deprecated(
        val message: String
    ) : ProcessedAnnotation()


    /**
     * Collection of serialised values for enum
     *
     * Assume kotlin enum: `enum class Foo { BAR }` where `Foo.BAR` is serialised as `Bar` then [mappings]
     * will contain entry `Foo.BAR to "Bar"`. Keys are limited to one enum type.
     */
    data class EnumNames(
        val mappings: Map<Any, String>
    ) : ProcessedAnnotation()
}
