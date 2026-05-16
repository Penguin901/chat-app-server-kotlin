package com.example.chatapp.security.jwt

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.stereotype.Component

/**
 * HTTP 요청의 헤더에서 Bearer 토큰을 추출합니다.
 */
@Component
class HttpBearerTokenResolver : BearerTokenResolver {

    override fun resolve(request: HttpServletRequest): String? {
        val header = request.getHeader(AUTHORIZATION_HEADER) ?: return null

        if (!header.startsWith(TOKEN_PREFIX)) {
            return null
        }

        return header.substring(TOKEN_PREFIX.length)
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val TOKEN_PREFIX = "Bearer "
    }
}