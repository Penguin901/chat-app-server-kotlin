package com.example.chatapp.friend

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FriendRepository : JpaRepository<Friend, Long> {
    fun findByUserId(userId: Long): List<Friend>

    fun existsByUserIdAndFriendUserId(userId: Long, friendUserId: Long): Boolean

    @Modifying
    @Query(
        """
    delete from Friend f
    where  f.user.id  = :userId and f.friendUser.id = :friendUserId
    
    """
    )
    fun deleteByUserIdAndFriendUserId(
        @Param("userId") userId: Long,
        @Param("friendUserId") friendUserId: Long
    )
}