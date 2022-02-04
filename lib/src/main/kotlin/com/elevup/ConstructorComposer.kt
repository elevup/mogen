package com.elevup

import com.elevup.annotation.model.MergedAnnotations
import com.elevup.model.Type

/**
 * Helps compose string that will eventually represent type's *default* constructor declaration
 */
interface ConstructorComposer {

    /**
     * Appends constructor header declaration.
     * Example: Kotlin `constructor(`, Java `public class [typeName](`, ...
     *
     * @param typeName - name of type
     */
    fun StringBuilder.appendHeader(typeName: String, indent: String?)

    /**
     * Appends one property of a constructor
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
     * Appends constructor footer declaration.
     * Example: Kotlin `)`, Java `) {}`, ...
     */
    fun StringBuilder.appendFooter(indent: String?)


}
