package com.example.chatapp.auth.dto.response

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String
)