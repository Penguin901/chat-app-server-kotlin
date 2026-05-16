package com.example.chatapp.auth

import com.example.chatapp.auth.dto.request.LoginRequest
import com.example.chatapp.auth.dto.request.RefreshTokenRequest
import com.example.chatapp.auth.dto.response.LoginResponse
import com.example.chatapp.auth.dto.response.RefreshTokenResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun authenticateAndReturnToken(@Valid @RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = loginUseCase.loginWithOAuth(request.idToken)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    fun reissueToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<RefreshTokenResponse> {
        val response = authService.reissueToken(request.refreshToken)
        return ResponseEntity.ok(response)
    }
}