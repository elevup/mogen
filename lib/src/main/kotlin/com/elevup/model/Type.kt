package com.elevup.model

import kotlin.reflect.KType

/**
 * In-memory definition of type
 */
sealed class Type {

    /**
     * Essentially any homogenous iterable = collection, array, ...
     * Homogenous = can contain children of the same (sub-)type
     */
    data class Iterable(
        val type: KType?
    ) : Type()

    /**
     * Reference to another type à la typealias
     */
    data class Reference(
        val name: String,
        val nullable: Boolean,
    ) : Type()

    /**
     * Primitive class - Int, Double, ...
     * This is a special case cause we eventually want to map them across languages (Kotlin's Int = Java's Integer)
     */
    data class Primitive(
        val name: String,
        val type: KType? = null,
        val nullable: Boolean = type?.isMarkedNullable == true,
    ) : Type()

    /**
     * Super-type of the universe - Java's Object, Kotlin's Any
     */
    object Any : Type()

}
