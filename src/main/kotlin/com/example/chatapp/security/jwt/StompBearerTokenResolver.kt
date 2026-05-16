package com.example.chatapp.security.jwt

import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component

/**
 * WebSocket(STOMP) 메시지 헤더에서 Bearer 토큰을 추출합니다.
 */
@Component
class StompBearerTokenResolver {

    fun resolve(accessor: StompHeaderAccessor): String? {
        val authorizationHeader = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER)
            ?: return null

        if (!authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return null
        }

        return authorizationHeader.substring(TOKEN_PREFIX.length)
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization";
        private const val TOKEN_PREFIX = "Bearer "
    }
}