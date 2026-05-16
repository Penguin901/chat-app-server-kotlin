package com.example.chatapp.auth.domain

import java.time.LocalDateTime

data class IssuedTokens(
    val accessToken: String,
    val accessTokenExpiresAt: LocalDateTime,
    val refreshToken: String,
    val refreshTokenExpiresAt: LocalDateTime
)