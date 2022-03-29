package com.elevup

import com.elevup.annotation.model.MergedAnnotations

/**
 * Helps compose string that will eventually represent enum declaration
 */
interface EnumComposer : Composer {

    /**
     * Appends enum header declaration.
     * Example: `enum class X {`
     *
     * @param typeName - name of type
     */
    fun StringBuilder.appendHeader(typeName: String)

    /**
     * Appends one member of enum.
     *
     * @param name - property name in JVM
     * @param annotations - enum property-bound annotations
     */
    fun StringBuilder.appendProperty(
        name: String,
        annotations: MergedAnnotations = MergedAnnotations(),
        indent: String? = null
    )

    /**
     * Appends enum footer declaration.
     * Example: `}`
     */
    fun StringBuilder.appendFooter()
}
