package com.elevup.models

sealed interface Optional<out T> {
    data class Some<out T>(val value: T) : Optional<T>
    data object None : Optional<Nothing>
}

data class PatchFormRequest(
    val cin: Optional<String?>,
    val zip: Optional<String>,
    val address: Optional<DataClass?>,
    val tags: Optional<List<String>>,
)
