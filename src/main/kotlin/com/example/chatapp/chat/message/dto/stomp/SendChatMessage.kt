package com.example.chatapp.chat.message.dto.stomp

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * WebSocket 통신에서 사용되는 DTO
 * - Client → Server : 메시지 전송 요청
 */
data class SendChatMessage(
    val chatRoomId: Long,

    @field:NotBlank
    @field:Size(max = 1000)
    val messageContent: String
)