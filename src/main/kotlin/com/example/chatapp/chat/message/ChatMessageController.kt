package com.example.chatapp.chat.message

import com.example.chatapp.chat.message.dto.stomp.SendChatMessage
import com.example.chatapp.chat.message.dto.response.ChatMessageResponse
import com.example.chatapp.security.UserPrincipal
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ChatMessageController(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val chatMessageUseCase: ChatMessageUseCase,
    private val chatMessageService: ChatMessageService,
) {
    // 채팅방의 메시지 조회 (클라이언트와의 데이터 동기화 시 사용)
    @GetMapping("/chat-messages/{chatRoomId}")
    fun getChatMessages(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable chatRoomId: Long
    ): List<ChatMessageResponse> {
        val currentUserId = userPrincipal.userId
        return chatMessageService.getChatMessages(currentUserId, chatRoomId)
    }

    @MessageMapping("/message")
    fun sendMessage(authentication: Authentication, @Payload payload: SendChatMessage) {
        val userPrincipal = authentication.principal as UserPrincipal
        val event = chatMessageUseCase.handleSendMessage(userPrincipal.userId, payload)

        simpMessagingTemplate.convertAndSend(
            "/sub/chatroom/${payload.chatRoomId}",
            event
        )
    }
}