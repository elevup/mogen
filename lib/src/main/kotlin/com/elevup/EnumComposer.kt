package com.elevup

import kotlin.reflect.KClass

/**
 * Helps compose string that will eventually represent enum declaration
 */
interface EnumComposer {

    /**
     * Appends enum header declaration.
     * Example: `enum class X {`
     *
     * @param typeName - name of type
     */
    fun StringBuilder.appendHeader(typeName: String)

    /**
     * Appends one member of enum. Supports serialised name using [klassNames].
     *
     * Assume kotlin enum: `enum class Foo { BAR }` where `Foo.BAR` is serialised as `Bar` then [klassNames]
     * must contain entry `Foo.BAR to "Bar"`.
     *
     * @param klass - original Kotlin klass
     * @param klassNames - mapping from enum constant instance to serialised name
     */
    fun StringBuilder.appendProperties(
        klass: KClass<*>,
        klassNames: Map<Any, String> = emptyMap(),
        indent: String? = null
    )

    /**
     * Appends enum footer declaration.
     * Example: `}`
     */
    fun StringBuilder.appendFooter()
}
