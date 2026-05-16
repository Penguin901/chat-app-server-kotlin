package com.example.chatapp.chat.message.dto.response

import com.example.chatapp.chat.message.ChatMessage
import java.time.LocalDateTime

/**
 * 클라이언트의 HTTP 요청에 응답하는 채팅 메시지 DTO
 */
data class ChatMessageResponse(
    val messageId: Long,
    val chatRoomId: Long,
    val senderId: Long,
    val messageContent: String,
    val sentAt: LocalDateTime
) {
    companion object {
        fun from(chatMessage: ChatMessage): ChatMessageResponse {
            return ChatMessageResponse(
                messageId = chatMessage.id!!,
                chatRoomId = chatMessage.chatRoom.id!!,
                senderId = chatMessage.sender.id!!,
                messageContent = chatMessage.messageContent,
                sentAt = chatMessage.sentAt

            )
        }
    }
}