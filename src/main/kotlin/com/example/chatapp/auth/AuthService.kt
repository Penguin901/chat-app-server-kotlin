package com.example.chatapp.auth

import com.example.chatapp.auth.domain.IssuedTokens
import com.example.chatapp.auth.dto.response.RefreshTokenResponse

interface AuthService {
    fun issueTokens(userId: Long): IssuedTokens
    fun reissueToken(refreshToken: String): RefreshTokenResponse
}