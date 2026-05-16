package com.example.chatapp.chat.member

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatMemberRepository : JpaRepository<ChatMember, Long> {

    fun findByChatRoomIdAndActiveFalse(chatRoomId: Long): List<ChatMember>

    fun findByChatRoomIdAndUserId(chatRoomId: Long, userId: Long): ChatMember?

    @Query("SELECT COUNT(cm) > 0 FROM ChatMember cm WHERE cm.id.chatRoomId = :chatRoomId AND cm.id.userId = :userId")
    fun existsByChatRoomIdAndUserId(chatRoomId: Long, userId: Long): Boolean

    fun existsByChatRoomIdAndActiveTrue(chatRoomId: Long): Boolean

    fun deleteByChatRoomId(chatRoomId: Long)
}