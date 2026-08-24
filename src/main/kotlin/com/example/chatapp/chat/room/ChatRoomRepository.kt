package com.example.chatapp.chat.room

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select cr from ChatRoom cr where cr.id = :id")
    fun findByIdForUpdate(id: Long): ChatRoom?

    @Query(
        """
        SELECT cm.chatRoom
        FROM ChatMember cm
        WHERE cm.user.id = :userId
          AND cm.active = TRUE
    
    """
    )
    fun findActiveChatRoomsByUserId(userId: Long): List<ChatRoom>

    fun findByDirectRoomKey(directRoomKey: String): ChatRoom?
}