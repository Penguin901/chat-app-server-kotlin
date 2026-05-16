package com.example.chatapp.chat.message

import com.example.chatapp.chat.member.ChatMemberRepository
import com.example.chatapp.chat.message.dto.response.ChatMessageResponse
import com.example.chatapp.chat.room.ChatRoomRepository
import com.example.chatapp.common.exception.ChatRoomException
import com.example.chatapp.common.exception.ErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatMessageServiceImpl(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatMemberRepository: ChatMemberRepository
) : ChatMessageService {

    override fun getChatMessages(currentUserId: Long, chatRoomId: Long): List<ChatMessageResponse> {
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw ChatRoomException(ErrorCode.CHAT_ROOM_NOT_FOUND)

        val chatMember = chatMemberRepository.findByChatRoomIdAndUserId(chatRoomId, currentUserId)
            ?: throw ChatRoomException(ErrorCode.NOT_A_MEMBER)

        val chatMessages = chatMessageRepository.findByChatRoomIdAndSentAtAfter(chatRoom.id!!, chatMember.joinedAt)

        return chatMessages.map { message ->
            ChatMessageResponse.from(message)
        }
    }
}