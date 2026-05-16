package com.example.chatapp.auth.domain

import com.example.chatapp.common.exception.AuthException
import com.example.chatapp.common.exception.ErrorCode
import java.time.LocalDateTime

// 발급된 refreshToken의 정보(DB 저장시 사용)
data class RefreshTokenInfo(
    val refreshToken: String,
    val refreshExpiration: LocalDateTime
) {
    init {
        if (refreshToken.isBlank()) {
            throw AuthException(ErrorCode.INVALID_TOKEN)
        }
        if (!refreshExpiration.isAfter(LocalDateTime.now())) {
            throw AuthException(ErrorCode.EXPIRED_TOKEN)
        }
    }
}