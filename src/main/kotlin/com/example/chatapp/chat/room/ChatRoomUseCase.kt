package com.example.chatapp.chat.room

import com.example.chatapp.chat.room.dto.request.CreateChatRoomRequest
import com.example.chatapp.chat.room.dto.response.CreateChatRoomResponse
import com.example.chatapp.common.exception.ChatRoomException
import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.common.exception.UserException
import com.example.chatapp.user.User
import com.example.chatapp.user.UserService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ChatRoomUseCase(
    private val userService: UserService,
    private val chatRoomService: ChatRoomService,
) {
    fun getOrCreateChatRoom(requesterId: Long, createChatRoomRequest: CreateChatRoomRequest): CreateChatRoomResponse {
        val participantIds = createChatRoomRequest.participantIds
        validateParticipants(participantIds, requesterId)

        val allUserIds = participantIds + requesterId
        val participants = userService.getUsersOrThrow(allUserIds)

        if (participants.size == 2) {
            val participantId1 = participants[0].id
            val participantId2 = participants[1].id

            val directRoomKey = chatRoomService.generateDirectRoomKey(participantId1!!, participantId2!!)
            val existingChatRoom = chatRoomService.findDirectChatRoom(directRoomKey)

            if (existingChatRoom != null) {
                return CreateChatRoomResponse.from(existingChatRoom, participants.map { user -> user.id!! })
            }

            val chatRoom = try {
                chatRoomService.createDirectChatRoom(directRoomKey, participants)
            } catch (e: DataIntegrityViolationException) {
                chatRoomService.findDirectChatRoom(directRoomKey)
                    ?: throw ChatRoomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
            }

            return CreateChatRoomResponse.from(chatRoom, participants.map { user -> user.id!! })
        }

        val chatRoom = chatRoomService.createGroupChatRoom(createChatRoomRequest.roomName, participants)

        return CreateChatRoomResponse.from(
            chatRoom,
            participants.map { user -> user.id!! }
        )
    }

    @Transactional
    fun leaveChatRoom(userId: Long, chatRoomId: Long) {
        //사용자가 방을 나감(해당 룸의 멤버 비활성화 또는 삭제)
        chatRoomService.removeChatMember(userId, chatRoomId)

        //멤버 삭제 후 해당 방에 멤버 없는 경우 해당 룸의 메세지 삭제 후 방 삭제
        chatRoomService.deleteRoomIfNoActiveMembers(chatRoomId)
    }

    private fun validateParticipants(participantIds: List<Long>, requesterId: Long) {
        // 중복포함된 유저 있는지 확인
        if (participantIds.size != participantIds.toSet().size) {
            throw UserException(ErrorCode.DUPLICATE_PARTICIPANTS)
        }

        // 자기자신 포함 확인
        if (requesterId in participantIds) {
            throw ChatRoomException(ErrorCode.CANNOT_ADD_SELF_AS_PARTICIPANT)
        }
    }
}

