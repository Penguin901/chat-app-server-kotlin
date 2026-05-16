package com.example.chatapp.common.exception

import org.springframework.http.ResponseEntity

data class ErrorResponse(
    private val message: String
) {
    companion object {
        // GlobalExceptionHandler에서 사용
        fun toResponseEntity(e: ErrorCode): ResponseEntity<ErrorResponse> {
            return ResponseEntity
                .status(e.httpStatus)
                .body(
                    ErrorResponse(
                        e.message
                    )
                )
        }

        // 스프링시큐리티에서 사용
        fun of(e: ErrorCode): ErrorResponse {
            return ErrorResponse(
                e.message
            )
        }
    }
}