package com.example.chatapp.chat.message

import com.example.chatapp.chat.message.dto.response.ChatMessageResponse
import com.example.chatapp.security.UserPrincipal
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/chat-messages")
@Tag(name = "Chat Message", description = "채팅 메세지 API")
class ChatMessageController(
    private val chatMessageService: ChatMessageService,
) {
    @Operation(summary = "메세지 조회", description = "주어진 채팅방(chatRoomId)의 메세지를 조회합니다.(클라이언트와의 메세지 데이터 동기화 시 사용)")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )

    @GetMapping("/{chatRoomId}")
    fun getChatMessages(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable chatRoomId: Long
    ): List<ChatMessageResponse> {
        val currentUserId = userPrincipal.userId
        return chatMessageService.getChatMessages(currentUserId, chatRoomId)
    }

}