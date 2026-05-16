package com.example.chatapp.chat.room.dto.request

import jakarta.validation.constraints.Size

data class CreateChatRoomRequest(
    val roomName: String?,

    @field:Size(min = 1)
    val participantIds: List<Long>

)