package com.example.chatapp.auth.dto.request

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank
    val idToken: String
)