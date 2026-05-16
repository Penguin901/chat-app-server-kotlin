package com.example.chatapp.chat.room

import com.example.chatapp.chat.room.dto.request.CreateChatRoomRequest
import com.example.chatapp.chat.room.dto.response.ChatPreviewResponse
import com.example.chatapp.chat.room.dto.response.CreateChatRoomResponse
import com.example.chatapp.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat-rooms")
class ChatRoomController(
    private val chatRoomUseCase: ChatRoomUseCase,
    private val chatRoomService: ChatRoomService
) {
    @GetMapping
    fun getChatRooms(@AuthenticationPrincipal userPrincipal: UserPrincipal): List<ChatPreviewResponse> {
        val userId = userPrincipal.userId
        return chatRoomService.getChatRoomsPreview(userId)
    }

    @PostMapping
    fun getOrCreateChatRoom(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: CreateChatRoomRequest
    ): CreateChatRoomResponse {
        val userId = userPrincipal.userId
        return chatRoomUseCase.getOrCreateChatRoom(userId, request)
    }

    @DeleteMapping("/{chatRoomId}/members/me")
    fun leaveChatRoom(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable("chatRoomId") chatRoomId: Long
    ): ResponseEntity<Void> {
        val userId = userPrincipal.userId
        chatRoomUseCase.leaveChatRoom(userId, chatRoomId)
        return ResponseEntity.noContent().build()
    }
}