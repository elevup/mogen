package com.elevup.models

data class ClassWithMaps(
    val dataClassString: Map<DataClass, String>,
    val stringAny: Map<String, Any>,
    val stringDataClass: Map<String, DataClass>,
    val stringLong: Map<String, Long>,
)