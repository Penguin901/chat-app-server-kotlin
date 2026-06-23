package com.example.chatapp.chat.message

import com.example.chatapp.chat.message.dto.stomp.SendChatMessage
import com.example.chatapp.security.UserPrincipal
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller

@Controller
class ChatMessageStompController(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val chatMessageUseCase: ChatMessageUseCase,
) {
    @MessageMapping("/message")
    fun sendMessage(authentication: Authentication, @Payload payload: SendChatMessage) {
        val userPrincipal = authentication.principal as UserPrincipal
        val event = chatMessageUseCase.handleSendMessage(userPrincipal.userId, payload)

        simpMessagingTemplate.convertAndSend(
            "/topic/chatroom/${payload.chatRoomId}",
            event
        )
    }
}