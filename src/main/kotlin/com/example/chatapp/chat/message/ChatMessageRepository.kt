package com.example.chatapp.chat.message

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {

    @Query(
        value = """
       select msg
       from ChatMessage msg
       where msg.chatRoom.id = :chatRoomId
         and msg.sentAt >= :joinedAt
       order by msg.sentAt desc
   
   """
    )
    fun findByChatRoomIdAndSentAtAfter(chatRoomId: Long, joinedAt: LocalDateTime): List<ChatMessage>

    fun deleteByChatRoomId(chatRoomId: Long)
}