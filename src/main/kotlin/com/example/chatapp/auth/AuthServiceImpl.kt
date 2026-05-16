package com.example.chatapp.auth

import com.example.chatapp.auth.domain.IssuedTokens
import com.example.chatapp.auth.domain.RefreshTokenInfo
import com.example.chatapp.auth.dto.response.RefreshTokenResponse
import com.example.chatapp.common.exception.AuthException
import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.security.jwt.JwtService
import com.example.chatapp.user.User
import com.example.chatapp.user.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthServiceImpl(
    private val userService: UserService,
    private val jwtService: JwtService
) : AuthService {

    override fun issueTokens(userId: Long): IssuedTokens {
        // TODO: 토큰 암호화하여 저장 필요
        val issuedTokens = jwtService.createTokens(userId)
        val refreshTokenInfo = RefreshTokenInfo(issuedTokens.refreshToken, issuedTokens.refreshTokenExpiresAt)

        userService.updateRefreshToken(userId, refreshTokenInfo)

        return issuedTokens
    }

    @Transactional
    override fun reissueToken(refreshToken: String): RefreshTokenResponse {
        val claims = jwtService.validateRefreshToken(refreshToken)
        val userId = claims.subject.toLong()
        val user: User = userService.getUserOrThrow(userId)

        if (refreshToken != user.refreshToken) {
            throw AuthException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        // 저장된 만료시간이 null인 경우 유효하지 않은 토큰으로 처리
        val expiration = user.refreshExpiration
            ?: throw AuthException(ErrorCode.INVALID_REFRESH_TOKEN)

        // 현재시간이 만료시간과 같거나 이전인 경우 만료된 토큰으로 처리
        if (!LocalDateTime.now().isBefore(expiration)) {
            throw AuthException(ErrorCode.EXPIRED_TOKEN)
        }

        val issuedTokens = issueTokens(userId)

        return RefreshTokenResponse(
            issuedTokens.accessToken,
            issuedTokens.refreshToken
        )
    }
}