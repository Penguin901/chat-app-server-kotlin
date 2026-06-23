package com.example.chatapp.auth

import com.example.chatapp.auth.dto.request.LoginRequest
import com.example.chatapp.auth.dto.request.RefreshTokenRequest
import com.example.chatapp.auth.dto.response.LoginResponse
import com.example.chatapp.auth.dto.response.RefreshTokenResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "사용자 인증 API")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val authService: AuthService
) {
    @Operation(summary = "로그인", description = "소셜로그인방식으로 로그인 후 사용자 계정을 생성하고, 사용자 인증을 위한 JSON Web Token(Access Token, Refresh Token)을 발급합니다.")
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = loginUseCase.loginWithOAuth(request.idToken)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "토큰 재발급", description = "Access Token이 만료된 경우, Refresh Token을 통해 사용자 인증을 위한 JSON Web Token(Access Token, Refresh Token)을 재발급합니다.")
    @PostMapping("/refresh")
    fun reissueToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<RefreshTokenResponse> {
        val response = authService.reissueToken(request.refreshToken)
        return ResponseEntity.ok(response)
    }

}