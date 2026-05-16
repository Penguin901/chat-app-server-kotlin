package com.example.chatapp.security.jwt

import com.example.chatapp.auth.domain.IssuedTokens
import com.example.chatapp.common.exception.AuthException
import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.security.UserPrincipal
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * 로그인 후 사용자 인증 토큰(JSON Web Token)을 생성하고, 클라이언트의 요청에 포함된 토큰의 유효성을 검증합니다. .
 */
@Component
class JwtService(@Value("\${spring.jwt.secret}") secret: String) {
    private val secretKey: SecretKey = SecretKeySpec(
        secret.toByteArray(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().algorithm
    )

    fun createTokens(userId: Long): IssuedTokens {
        val now = LocalDateTime.now()
      //  val LocalDateTime accessTokenExpirationAt = now.plusHours(1);
        val accessTokenExpirationAt = now.plusMinutes(1)
        val refreshTokenExpirationAt = now.plusDays(5)

        val issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant())
        val accessTokenExpirationDate = Date.from(accessTokenExpirationAt.atZone(ZoneId.systemDefault()).toInstant())
        val refreshTokenExpirationDate = Date.from(refreshTokenExpirationAt.atZone(ZoneId.systemDefault()).toInstant())

        val accessToken = Jwts.builder()
            .subject(userId.toString())
            .claim(
                "role",
                listOf("USER_EDIT")
            )
            .claim("type", ACCESS_TOKEN_TYPE)
            .issuedAt(issuedAt)
            .expiration(accessTokenExpirationDate)
            .signWith(secretKey)
            .compact()

        val refreshToken = Jwts.builder()
            .subject(userId.toString())
            .claim(
                "role",
                listOf("USER_EDIT")
            )
            .claim("type", REFRESH_TOKEN_TYPE)
            .issuedAt(issuedAt)
            .expiration(refreshTokenExpirationDate)
            .signWith(secretKey)
            .compact()

        return IssuedTokens(
            accessToken,
            accessTokenExpirationAt,
            refreshToken,
            refreshTokenExpirationAt
        )
    }

    fun validateAccessToken(accessToken: String): Claims {
        val claims = verifyToken(accessToken)
        val type = claims["type"] as? String ?: throw AuthException(ErrorCode.INVALID_TOKEN)

        if (type != ACCESS_TOKEN_TYPE) {
            throw AuthException(ErrorCode.INVALID_TOKEN_TYPE)
        }

        return claims
    }

    fun validateRefreshToken(refreshToken: String): Claims {
        val claims = verifyToken(refreshToken)
        val type = claims["type"] as? String ?: throw AuthException(ErrorCode.INVALID_TOKEN)

        if (type != REFRESH_TOKEN_TYPE) {
            throw AuthException(ErrorCode.INVALID_TOKEN_TYPE)
        }

        return claims
    }

    fun getAuthentication(claims: Claims): Authentication {
        val userId = claims.subject.toLong()
        val authorities = listOf(SimpleGrantedAuthority("USER_EDIT"))
        val userPrincipal = UserPrincipal(userId, authorities)

        return UsernamePasswordAuthenticationToken(
            userPrincipal, // Principal
            null,
            userPrincipal.authorities
        )
    }

    private fun verifyToken(token: String): Claims {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            throw AuthException(ErrorCode.EXPIRED_TOKEN)
        } catch (e: JwtException) {
            throw AuthException(ErrorCode.INVALID_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw AuthException(ErrorCode.INVALID_TOKEN)
        }
    }

    companion object {
        private const val ACCESS_TOKEN_TYPE = "ACCESS"
        private const val REFRESH_TOKEN_TYPE = "REFRESH"
    }
}