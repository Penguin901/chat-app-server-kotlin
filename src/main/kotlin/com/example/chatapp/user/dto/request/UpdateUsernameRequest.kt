package com.example.chatapp.user.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateUsernameRequest(
    @field:NotBlank
    @field:Size(min = 4, max = 20)
    val username: String
)