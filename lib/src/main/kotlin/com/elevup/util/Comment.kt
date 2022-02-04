package com.elevup.util

/**
 * Wraps string into comment using /* ... */
 * Note that this suits only for select programming languages!
 */
fun String.wrapIntoComment() = prependIndent(" * ").let { "/**\n$it\n */" }
