package com.example.chatapp.websocket

import com.example.chatapp.chat.room.ChatRoomService
import com.example.chatapp.common.exception.AuthException
import com.example.chatapp.common.exception.ChatRoomException
import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.security.UserPrincipal
import com.example.chatapp.security.jwt.JwtService
import com.example.chatapp.security.jwt.StompBearerTokenResolver
import lombok.RequiredArgsConstructor
import mu.KotlinLogging
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class StompChannelInterceptor(
    private val stompBearerTokenResolver: StompBearerTokenResolver,
    private val jwtService: JwtService,
    private val chatRoomService: ChatRoomService,
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (accessor != null) {
            when (accessor.command) {
                StompCommand.CONNECT -> {
                    handleConnect(accessor)
                }

                StompCommand.SUBSCRIBE -> {
                    handleSubscribe(accessor)
                }

                StompCommand.SEND -> {
                    // ChatMessageUseCase에서 멤버 검증하므로 별도 처리 안 함
                }

                else -> {}
            }
        }
        return message
    }

    private fun handleConnect(accessor: StompHeaderAccessor) {
        val token = stompBearerTokenResolver.resolve(accessor) ?: throw AuthException(ErrorCode.INVALID_TOKEN)
        // val token = stompBearerTokenResolver.resolve(accessor)?:throw AccessDeniedException("token is null")

        try {
            val claims = jwtService.validateAccessToken(token)
            val authentication = jwtService.getAuthentication(claims)
            accessor.user = authentication
        } catch (e: AuthException) {
            throw AccessDeniedException("Invalid or expired token", e)
        }
    }

    private fun handleSubscribe(accessor: StompHeaderAccessor) {
        validateMember(accessor)
    }

    private fun validateMember(accessor: StompHeaderAccessor) {
        val authentication = accessor.user as? Authentication
            ?: throw AccessDeniedException("Unauthenticated")

        val chatRoomId = extractChatRoomId(accessor.destination)
        val userId = extractUserId(authentication)

        try {
            chatRoomService.validateMember(chatRoomId, userId)
        } catch (e: ChatRoomException) {
            throw AccessDeniedException("Not a member")
        }
    }

    private fun extractUserId(authentication: Authentication): Long {
        return (authentication.principal as UserPrincipal).userId
    }

    private fun extractChatRoomId(destination: String?): Long {
        if (destination.isNullOrBlank() || !destination.startsWith("/sub/chatroom/")) {
            throw AccessDeniedException("Invalid destination")
        }

        val chatRoomId = destination.substringAfter("/sub/chatroom/")
        return chatRoomId.toLongOrNull() ?: throw AccessDeniedException("Invalid destination")
    }
}