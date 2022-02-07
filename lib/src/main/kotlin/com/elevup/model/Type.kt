package com.elevup.model

import kotlin.reflect.KType

/**
 * In-memory definition of type
 */
sealed class Type(
    open val nullable: Boolean
) {

    /**
     * Essentially any homogenous iterable = collection, array, ...
     * Homogenous = can contain children of the same (sub-)type
     */
    data class Iterable(
        val type: KType?,
        override val nullable: Boolean,
    ) : Type(nullable)

    /**
     * Reference to another type à la typealias
     */
    data class Reference(
        val name: String,
        override val nullable: Boolean,
    ) : Type(nullable)

    /**
     * Primitive class - Int, Double, ...
     * This is a special case cause we eventually want to map them across languages (Kotlin's Int = Java's Integer)
     */
    data class Primitive(
        val name: String,
        val type: KType? = null,
        override val nullable: Boolean = type?.isMarkedNullable == true,
    ) : Type(nullable)

    /**
     * Super-type of the universe - Java's Object, Kotlin's Any
     */
    object Any : Type(false)

}
