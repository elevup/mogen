package com.elevup

/**
 * Helps compose string that will eventually represent typealias declaration
 */
interface TypealiasComposer {

    /**
     * Appends typealias declaration to builder
     *
     * @param aliasName - of alias
     * @param originalName - original type's name that is referenced
     * @param comment - optional comment that will be appended on top of declaration
     */
    fun StringBuilder.appendTypealias(
        aliasName: String,
        originalName: String,
        comment: String? = null,
        indent: String? = null,
    )

}
