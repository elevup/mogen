package com.elevup.models.request

import com.elevup.models.User
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class CreateUserRequest(
    @Size(min = 1, max = 100)
    @Pattern(regexp = "[a-zA-Z]*")
    @JsonProperty("first_name")
    val firstName: String?,

    @Size(min = 1, max = 100)
    @Pattern(regexp = "[a-zA-Z]*")
    @JsonProperty("last_name")
    val lastName: String?,

    @JsonProperty("dominant_hand")
    val dominantHand: User.Hand?,

    val birthday: LocalDateTime?,
    val balance: BigDecimal,
)
