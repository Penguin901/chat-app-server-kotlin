package com.example.chatapp.chat.room.dto.response

import com.example.chatapp.chat.room.ChatRoom
import java.time.LocalDateTime

data class ChatPreviewResponse(
    val id: Long,
    val roomName: String?,
    val profileImageUrl: String?,
    val lastMessageContent: String,
    val lastMessageAt: LocalDateTime
) {
    companion object {
        fun from(chatRoom: ChatRoom): ChatPreviewResponse {
            return ChatPreviewResponse(
                id = chatRoom.id!!,
                roomName = chatRoom.roomName,
                profileImageUrl = null, // 일단 null로, 클라이언트에서 안 보냄
                lastMessageContent = chatRoom.lastMessageContent,
                lastMessageAt = chatRoom.lastMessageAt
            )
        }
    }
}

