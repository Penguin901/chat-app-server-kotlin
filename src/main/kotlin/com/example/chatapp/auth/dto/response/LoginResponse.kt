package com.example.chatapp.auth.dto.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val accountInfo: AccountInfo,
)


// val accessTokenExpiresAt: LocalDateTime
// val refreshTokenExpiresAt: LocalDateTime

