package com.example.chatapp.common.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.security.GeneralSecurityException

@RestControllerAdvice
class GlobalExceptionHandler {

    // HTTP 요청 body가 없거나 JSON 형식에 맞지 않는 경우
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_REQUEST)
    }

    // DTO의 @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_REQUEST)
    }

    // PathVariable 또는 RequestParam 검증 실패
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException?): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_REQUEST)
    }

    // 유효하지 않은 값이 메서드 인자로 전달된 경우
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException?): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_REQUEST)
    }

    // 예상하지 못한 JwtException
    @ExceptionHandler(JwtException::class)
    fun handleJwtException(e: JwtException): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(ErrorCode.UNAUTHORIZED_ACCESS)
    }

    // 예상하지 못한 GeneralSecurityException
    @ExceptionHandler(GeneralSecurityException::class)
    fun handleGeneralSecurityException(e: GeneralSecurityException?): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(ErrorCode.UNAUTHORIZED_ACCESS)
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(e.errorCode)
    }

    // DB 제약조건 위반
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(e: DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_REQUEST)
    }

    @ExceptionHandler(JpaSystemException::class)
    fun handleDataIntegrityViolationException(e: JpaSystemException?): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR)
    }

    // 예상하지 못한 예외
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR)
    }

}