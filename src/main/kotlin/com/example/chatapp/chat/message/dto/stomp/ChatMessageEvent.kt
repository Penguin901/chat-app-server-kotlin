package com.example.chatapp.chat.message.dto.stomp

import com.example.chatapp.chat.message.ChatMessage
import com.example.chatapp.chat.message.dto.response.SenderInfo
import java.time.LocalDateTime

/**
 * WebSocket 통신에서 사용되는 DTO
 * - Server → Client : 메시지 브로드캐스트 응답
 */
data class ChatMessageEvent(
    val messageId: Long,
    val chatRoomId: Long,
    val sender: SenderInfo,
    val messageContent: String,
    val sentAt: LocalDateTime
) {
    companion object {
        fun from(chatMessage: ChatMessage): ChatMessageEvent {
            return ChatMessageEvent(
                messageId = requireNotNull(chatMessage.id),
                chatRoomId = requireNotNull(chatMessage.chatRoom.id),
                sender = SenderInfo.from(chatMessage.sender),
                messageContent = chatMessage.messageContent,
                sentAt = chatMessage.sentAt
            )
        }
    }
}