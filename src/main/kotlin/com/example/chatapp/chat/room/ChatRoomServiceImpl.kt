package com.example.chatapp.chat.room

import com.example.chatapp.chat.member.ChatMember
import com.example.chatapp.chat.member.ChatMemberRepository
import com.example.chatapp.chat.message.ChatMessageRepository
import com.example.chatapp.chat.room.dto.response.ChatPreviewResponse
import com.example.chatapp.common.exception.ChatRoomException
import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.user.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomServiceImpl(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMemberRepository: ChatMemberRepository,
    private val chatMessageRepository: ChatMessageRepository
) : ChatRoomService {

    @Transactional(readOnly = true)
    override fun getChatRoomsPreview(currentUserId: Long): List<ChatPreviewResponse> {
        val chatRooms = chatRoomRepository.findActiveChatRoomsByUserId(currentUserId)

        return chatRooms.map { chatRoom ->
            ChatPreviewResponse.from(
                chatRoom,
            )
        }
    }

    // 기존 채팅방이 존재하는 경우
    override fun findChatRoomOrThrow(chatRoomId: Long): ChatRoom {
        return chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw ChatRoomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
    }

    override fun ensureChatRoom(participants: List<User>, roomName: String?): ChatRoom {
        if (participants.size == 2) {
            return ensureDirectChatRoom(participants)
        }

        return createGroupChatRoom(roomName, participants)
    }


    override fun validateMember(chatRoomId: Long, senderId: Long) {
        val isMember: Boolean = chatMemberRepository.existsByChatRoomIdAndUserId(chatRoomId, senderId)
        if (!isMember) {
            throw ChatRoomException(ErrorCode.NOT_A_MEMBER)
        }
    }

    override fun activateInactiveMembers(chatRoomId: Long) {
        val inactiveMembers: List<ChatMember> =
            chatMemberRepository.findByChatRoomIdAndActiveFalse(chatRoomId)

        for (member in inactiveMembers) {
            member.activate()
        }
    }

    override fun removeChatMember(currentUserId: Long, chatRoomId: Long) {
        val member: ChatMember = chatMemberRepository
            .findByChatRoomIdAndUserId(chatRoomId, currentUserId)
            ?: throw ChatRoomException(ErrorCode.NOT_A_MEMBER)

        val chatRoom = findChatRoomOrThrow(chatRoomId)

        // 1대1 채팅 -> 비활성화(동일 사용자와 방 재생성시 기존 방 사용하기 위해)
        if (chatRoom.roomType == ChatRoom.RoomType.DIRECT) {
            member.deactivate()
        } else { // 그룹채팅 -> 사용자가 방을 나가면 멤버에서 삭제
            chatMemberRepository.delete(member)
        }
    }

    override fun deleteRoomIfNoActiveMembers(chatRoomId: Long) {
        val hasActiveMember: Boolean =
            chatMemberRepository.existsByChatRoomIdAndActiveTrue(chatRoomId)

        if (!hasActiveMember) {
            chatMemberRepository.deleteByChatRoomId(chatRoomId)
            chatMessageRepository.deleteByChatRoomId(chatRoomId)
            chatRoomRepository.deleteById(chatRoomId)
        }
    }

    private fun ensureDirectChatRoom(participants: List<User>): ChatRoom {
        val user1 = participants[0].id
        val user2 = participants[1].id

        val directRoomKey = generateDirectRoomKey(user1!!, user2!!)
        val existingChatRoom = chatRoomRepository.findByDirectRoomKey(directRoomKey)

        if (existingChatRoom != null) return existingChatRoom

        // TODO: 두명의 사용자가 동시에 생성 요청하는 경우(directRoomKey 제약 조건 위반) 처리 필요
        val chatRoom = ChatRoom.createDirect(directRoomKey)
        chatRoomRepository.save(chatRoom)
        addChatRoomMembers(chatRoom, participants)

        return chatRoom
    }

    private fun generateDirectRoomKey(userId1: Long, userId2: Long): String {
        if (userId1 == userId2) {
            throw ChatRoomException(ErrorCode.CANNOT_ADD_SELF_AS_PARTICIPANT)
        }

        val minId = minOf(userId1, userId2)
        val maxId = maxOf(userId1, userId2)

        return "${minId}_${maxId}"
    }

    private fun createGroupChatRoom(roomName: String?, participants: List<User>): ChatRoom {
        val chatRoom = ChatRoom.createGroup(roomName)
        chatRoomRepository.save(chatRoom)
        addChatRoomMembers(chatRoom, participants)

        return chatRoom
    }

    private fun addChatRoomMembers(chatRoom: ChatRoom, participants: List<User>) {
        val newMembers = participants.map { participant ->
            ChatMember.create(chatRoom, participant)
        }

        chatMemberRepository.saveAll(newMembers)
    }
}