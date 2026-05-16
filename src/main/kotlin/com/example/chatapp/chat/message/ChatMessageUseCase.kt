package com.example.chatapp.chat.message

import com.example.chatapp.chat.message.dto.stomp.ChatMessageEvent
import com.example.chatapp.chat.room.ChatRoomService
import com.example.chatapp.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ChatMessageUseCase(
    private val chatRoomService: ChatRoomService,
    private val userService: UserService,
    private val chatMessageRepository: ChatMessageRepository
) {
    fun handleSendMessage(
        senderId: Long,
        request: com.example.chatapp.chat.message.dto.stomp.SendChatMessage
    ): ChatMessageEvent {
        val chatRoom = chatRoomService.findChatRoomOrThrow(request.chatRoomId)
        val sender = userService.getUserOrThrow(senderId)

        chatRoomService.validateMember(chatRoom.id!!, senderId)

        val chatMessage = ChatMessage.create(chatRoom, sender, request.messageContent)
        chatMessageRepository.save(chatMessage)

        chatRoom.updateLastMessageContent(chatMessage)

        // 1대1채팅시에만 멤버 활성화 (그룹채팅은 새멤버로 추가)
        chatRoomService.activateInactiveMembers(chatRoom.id)

        return ChatMessageEvent.from(chatMessage)
    }
}