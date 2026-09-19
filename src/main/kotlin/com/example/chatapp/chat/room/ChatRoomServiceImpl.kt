package com.example.chatapp.chat.room

import com.example.chatapp.chat.member.ChatMember
import com.example.chatapp.chat.member.ChatMemberRepository
import com.example.chatapp.chat.message.ChatMessageRepository
import com.example.chatapp.chat.room.dto.response.ChatPreviewResponse
import com.example.chatapp.common.exception.ChatRoomException
import com.example.chatapp.common.exception.ErrorCode
import com.example.chatapp.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatRoomServiceImpl(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMemberRepository: ChatMemberRepository,
    private val chatMessageRepository: ChatMessageRepository,
) : ChatRoomService {

    override fun getChatRoomsPreview(currentUserId: Long): List<ChatPreviewResponse> {
        val chatRooms = chatRoomRepository.findActiveChatRoomsByUserId(currentUserId)

        val roomIds = chatRooms.map { it.id!! }

        val latestMessages =
            chatMessageRepository.findLatestMessagesByChatRoomIds(roomIds)

        val latestMessageByRoomId =
            latestMessages.associateBy { it.chatRoom.id!! }

        return chatRooms.map { chatRoom ->
            val lastMessage = latestMessageByRoomId[chatRoom.id!!]

            ChatPreviewResponse.from(
                chatRoom,
                lastMessage!!
            )
        }
    }
    // 기존 채팅방이 존재하는 경우
    override fun findChatRoomForUpdate(chatRoomId: Long): ChatRoom {
        return chatRoomRepository.findByIdForUpdate(chatRoomId)
            ?: throw ChatRoomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
    }

    override fun findDirectChatRoom(directRoomKey: String): ChatRoom? {
        return chatRoomRepository.findByDirectRoomKey(directRoomKey)
    }

    @Transactional
    override fun createDirectChatRoom(directRoomKey: String, participants: List<User>): ChatRoom {
        val chatRoom = ChatRoom.createDirect(directRoomKey)
        chatRoomRepository.save(chatRoom)

        addChatRoomMembers(chatRoom, participants)

        return chatRoom
    }

    override fun generateDirectRoomKey(participantId1: Long, participantId2: Long): String {
        if (participantId1 == participantId2) {
            throw ChatRoomException(ErrorCode.CANNOT_ADD_SELF_AS_PARTICIPANT)
        }

        val minId = minOf(participantId1, participantId2)
        val maxId = maxOf(participantId1, participantId2)

        return "${minId}_${maxId}"
    }

    @Transactional
    override fun createGroupChatRoom(roomName: String?, participants: List<User>): ChatRoom {
        val chatRoom = ChatRoom.createGroup(roomName)
        chatRoomRepository.save(chatRoom)

        addChatRoomMembers(chatRoom, participants)

        return chatRoom
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

        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow {
            ChatRoomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
        }

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

    private fun addChatRoomMembers(chatRoom: ChatRoom, participants: List<User>) {
        val newMembers = participants.map { participant ->
            ChatMember.create(chatRoom, participant)
        }

        chatMemberRepository.saveAll(newMembers)
    }
}