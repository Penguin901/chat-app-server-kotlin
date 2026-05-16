package com.example.chatapp.security.config

import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.common.exception.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component // 인증이 안 된 경우 401
class CustomAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: AuthenticationException
    ) {
        val body = ErrorResponse.of(ErrorCode.UNAUTHORIZED_ACCESS)

        response.apply {
            status = HttpServletResponse.SC_UNAUTHORIZED
            characterEncoding = StandardCharsets.UTF_8.name()
            contentType = MediaType.APPLICATION_JSON_VALUE
            writer.write(objectMapper.writeValueAsString(body))
        }
    }
}