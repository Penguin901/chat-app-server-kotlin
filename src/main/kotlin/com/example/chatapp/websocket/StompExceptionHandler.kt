package com.example.chatapp.websocket

import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.common.exception.ErrorResponse
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
class StompExceptionHandler {

    @MessageExceptionHandler(Exception::class)
    @SendToUser("/queue/errors", broadcast = false)
    fun exceptionHandler(e: Exception): ErrorResponse {
        return ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}