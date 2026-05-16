package com.example.chatapp.chat.room

import com.example.chatapp.chat.room.dto.response.ChatPreviewResponse
import com.example.chatapp.user.User

interface ChatRoomService {

    fun getChatRoomsPreview(currentUserId: Long): List<ChatPreviewResponse>

    fun findChatRoomOrThrow(chatRoomId: Long): ChatRoom

    fun ensureChatRoom(participants: List<User>, roomName: String?): ChatRoom

    fun validateMember(chatRoomId: Long, senderId: Long)

    fun activateInactiveMembers(chatRoomId: Long)

    fun removeChatMember(currentUserId: Long, chatRoomId: Long)

    fun deleteRoomIfNoActiveMembers(chatRoomId: Long)
}