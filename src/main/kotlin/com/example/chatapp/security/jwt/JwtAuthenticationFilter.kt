package com.example.chatapp.security.jwt

import com.example.chatapp.common.exception.AuthException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val httpBearerTokenResolver: HttpBearerTokenResolver,
    private val jwtService: JwtService,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val excludedPaths = listOf(
            AntPathRequestMatcher("/auth/**"),
            AntPathRequestMatcher("/ws/**"),
            AntPathRequestMatcher("/swagger-ui/**"),
            AntPathRequestMatcher("/v3/api-docs/**")
        )

        return excludedPaths.any {
            it.matches(request)
        }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = httpBearerTokenResolver.resolve(request)

        if (token != null) {
            try {
                val claims = jwtService.validateAccessToken(token)
                val authentication = jwtService.getAuthentication(claims)

                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: AuthException) {
                // 인증 실패시 인증 정보 초기화
                SecurityContextHolder.clearContext()
                throw AuthenticationCredentialsNotFoundException("Authentication failed", e)
            }
        }

        filterChain.doFilter(request, response)
    }
}