package com.example.chatapp.auth

import com.example.chatapp.auth.dto.response.AccountInfo
import com.example.chatapp.auth.dto.response.LoginResponse
import com.example.chatapp.auth.oauth.GoogleTokenVerifier
import com.example.chatapp.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * OAuth 로그인 요청을 처리하고, 사용자 계정을 생성 또는 조회하여 사용자인증에 필요한 토큰을 발급합니다.
 */
@Component
class LoginUseCase(
    private val googleTokenVerifier: GoogleTokenVerifier,
    private val userService: UserService,
    private val authService: AuthService
) {
    @Transactional
    fun loginWithOAuth(idToken: String): LoginResponse {
        val oauthUser = googleTokenVerifier.verifyIdToken(idToken)
        val user = userService.findOrCreateUserByOAuth(oauthUser)

        val issuedTokens = authService.issueTokens(user.id!!)
        val accountInfo = AccountInfo(user.id, user.email)

        return LoginResponse(
            issuedTokens.accessToken,  //issuedTokens.accessTokenExpiresAt(),
            issuedTokens.refreshToken,  //issuedTokens.refreshTokenExpiresAt(),
            accountInfo
        )
    }
}