package com.elevup.model

import kotlin.reflect.KClass

/**
 * In-memory definition of typealias
 */
data class Typealias(
    /**
     * Referenced class
     */
    val localClass: KClass<*>,
    /**
     * Typealias name
     */
    val name: String,
    /**
     * Comment that will be flushed to output
     */
    val comment: String? = null,
)
