package com.elevup.models

data class SealedUsage(
    val parent: SealedClass,
    val typeA: SealedClass.A,
    val typeB: SealedClass.B,
)

sealed interface SealedClass {
    val id: Long
    val type: String

    data class A(
        override val id: Long,
        val customA: String,
    ) : SealedClass {
        override val type = "A"
    }

    data class B(
        override val id: Long,
        val customB: String,
    ) : SealedClass {
        override val type = "B"
    }
}