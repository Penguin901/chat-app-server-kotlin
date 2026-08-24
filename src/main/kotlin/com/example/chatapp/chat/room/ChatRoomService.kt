package com.example.chatapp.chat.room

import com.example.chatapp.chat.room.dto.response.ChatPreviewResponse
import com.example.chatapp.user.User

interface ChatRoomService {

    fun getChatRoomsPreview(currentUserId: Long): List<ChatPreviewResponse>

    fun findChatRoomForUpdate(chatRoomId: Long): ChatRoom

    fun findDirectChatRoom(directRoomKey: String): ChatRoom?

    fun createDirectChatRoom(directRoomKey: String, participants: List<User>): ChatRoom

    fun generateDirectRoomKey(participantId1: Long, participantId2: Long): String

    fun createGroupChatRoom(roomName: String?, participants: List<User>): ChatRoom

    fun validateMember(chatRoomId: Long, senderId: Long)

    fun activateInactiveMembers(chatRoomId: Long)

    fun removeChatMember(currentUserId: Long, chatRoomId: Long)

    fun deleteRoomIfNoActiveMembers(chatRoomId: Long)
}