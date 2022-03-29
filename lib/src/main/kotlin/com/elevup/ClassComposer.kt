package com.elevup

import com.elevup.annotation.model.MergedAnnotations
import com.elevup.model.Type

/**
 * Helps compose string that will eventually represent class that is not an enum declaration
 */
interface ClassComposer : Composer {

    /**
     * Appends class header declaration.
     * Example: Kotlin `data class [typeName](`
     *
     * @param typeName - name of type
     */
    fun StringBuilder.appendHeader(typeName: String)

    /**
     * Appends one property of a class
     *
     * @param name - name of property
     * @param type - local type of property
     * @param formatType - function that converts type to a String
     * @param annotations - optional annotations
     */
    fun StringBuilder.appendProperty(
        name: String,
        type: Type,
        formatType: (Type) -> String,
        annotations: MergedAnnotations = MergedAnnotations(),
        indent: String? = null
    )

    /**
     * Appends class footer declaration.
     * Example: Kotlin `)`
     */
    fun StringBuilder.appendFooter()


}
