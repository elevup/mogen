package com.elevup.annotation.model

data class MergedAnnotations(
    val fieldName: String? = null,
    val min: Long? = null,
    val max: Long? = null,
    val regex: String? = null,
    val deprecated: String? = null,
    // Mapping <EnumItem, SerializedFieldName>
    val enumNames: Map<Any, String>? = null,
)
