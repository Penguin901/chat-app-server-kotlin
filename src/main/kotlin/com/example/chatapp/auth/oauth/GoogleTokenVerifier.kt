package com.example.chatapp.auth.oauth

import com.example.chatapp.common.exception.AuthException
import com.example.chatapp.common.exception.ErrorCode
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import java.security.GeneralSecurityException

/**
 * Google ID Token 검증 클래스
 *
 * 클라이언트로부터 전달받은 Google의 ID Token을 검증하고 사용자 계정정보 생성에 필요한 데이터를 반환합니다.
 */
@Component
class GoogleTokenVerifier(
    @Value("\${google.web.client-id}")
    private val googleClientId: String
) {
    private val verifier = GoogleIdTokenVerifier.Builder(
        NetHttpTransport(),
        GsonFactory.getDefaultInstance()
    )
        .setAudience(listOf(googleClientId))
        .setIssuers(listOf("https://accounts.google.com", "accounts.google.com"))
        .build()

    fun verifyIdToken(idToken: String): OAuthUser {
        val googleIdToken = try {
            // 잘못된 토큰 또는 만료된 토큰의 경우 null 반환
            verifier.verify(idToken) ?: throw AuthException(ErrorCode.INVALID_OAUTH_TOKEN)
        } catch (e: GeneralSecurityException) {
            throw AuthException(ErrorCode.OAUTH_SERVER_ERROR)
        } catch (e: IOException) {
            throw AuthException(ErrorCode.OAUTH_SERVER_ERROR)
        }

        val payload = googleIdToken.payload

        if (!payload.emailVerified) {
            throw AuthException(ErrorCode.INVALID_OAUTH_TOKEN)
        }

        return GoogleOAuthUser(
            payload.subject,
            payload.email,
            payload.emailVerified
        )
    }
}