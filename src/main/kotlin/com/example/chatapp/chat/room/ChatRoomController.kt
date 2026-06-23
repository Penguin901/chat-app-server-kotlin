package com.example.chatapp.chat.room

import com.example.chatapp.chat.room.dto.request.CreateChatRoomRequest
import com.example.chatapp.chat.room.dto.response.ChatPreviewResponse
import com.example.chatapp.chat.room.dto.response.CreateChatRoomResponse
import com.example.chatapp.security.UserPrincipal
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat-rooms")
@Tag(name = "Chat Room", description = "채팅방 API")
class ChatRoomController(
    private val chatRoomUseCase: ChatRoomUseCase,
    private val chatRoomService: ChatRoomService
) {
    @Operation(summary = "채팅방 목록 조회", description = "현재 로그인한 사용자의 채팅방 목록을 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )
    @GetMapping
    fun getChatRooms(@AuthenticationPrincipal userPrincipal: UserPrincipal): List<ChatPreviewResponse> {
        val userId = userPrincipal.userId
        return chatRoomService.getChatRoomsPreview(userId)
    }

    @Operation(summary = "채팅방 조회 및 생성", description = "현재 로그인한 사용자의 채팅방이 존재하는 경우 해당 방을 반환하고, 존재하지 않는 경우 새로운 방을 생성하여 반환합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )
    @PostMapping
    fun getOrCreateChatRoom(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: CreateChatRoomRequest
    ): CreateChatRoomResponse {
        val userId = userPrincipal.userId
        return chatRoomUseCase.getOrCreateChatRoom(userId, request)
    }

    @Operation(summary = "채팅방 나가기", description = "주어진 채팅방(chatRoomId)을 현재 로그인한 사용자의 채팅방 목록에서 삭제합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "나가기 성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패")
    )
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