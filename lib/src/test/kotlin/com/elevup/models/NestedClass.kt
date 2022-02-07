package com.elevup.models

data class Parent(
    val id : Long,
    val child: Child
) {

    data class Child(
        val id: String
    )

}
