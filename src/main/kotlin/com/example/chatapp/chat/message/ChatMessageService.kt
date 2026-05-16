package com.example.chatapp.chat.message

import com.example.chatapp.chat.message.dto.response.ChatMessageResponse

interface ChatMessageService {
    fun getChatMessages(currentUserId: Long, chatRoomId: Long): List<ChatMessageResponse>
}