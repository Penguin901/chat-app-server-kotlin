package com.example.chatapp.chat.room.dto.response

import com.example.chatapp.chat.room.ChatRoom

data class CreateChatRoomResponse(
    val id: Long,
    val roomName: String?,
    val participantIds: List<Long>
) {
    companion object {
        fun from(chatRoom: ChatRoom, participantIds: List<Long>): CreateChatRoomResponse {
            return CreateChatRoomResponse(
                id = chatRoom.id!!,
                roomName = chatRoom.roomName,
                participantIds = participantIds
            )
        }
    }
}