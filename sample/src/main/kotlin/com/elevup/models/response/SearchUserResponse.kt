package com.elevup.models.response

import com.elevup.models.User

data class SearchUserResponse(
    val results: List<User>,
    val totalCount: Int,
)
