package com.elevup.models

sealed interface SealedWithoutProperties {
    data class Value(val value: Int) : SealedWithoutProperties
    data object Empty : SealedWithoutProperties
}

sealed class SealedWithConstructorProperty(val id: Long) {
    class Child(id: Long, val name: String) : SealedWithConstructorProperty(id)
}
