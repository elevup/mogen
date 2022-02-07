package com.elevup.models

class ClassWithIterables(
    val iterable: List<String>,
    val optionalArguments: Array<String?>,
    val optionalIterable: Set<String>?,
    val optionalEverything: List<String?>?,
)
