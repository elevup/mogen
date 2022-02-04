package com.elevup.models

import com.elevup.UserId
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class User(
    val id: UserId,
    @JsonProperty("first_name")
    val firstName: String?,
    @JsonProperty("last_name")
    val lastName: String?,
    @JsonProperty("dominant_hand")
    val dominantHand: Hand?,
    val birthday: LocalDateTime?,
    val balance: BigDecimal,
) {

    enum class Hand {
        @JsonProperty("r")
        RIGHT,
        @JsonProperty("l")
        LEFT;
    }

}
