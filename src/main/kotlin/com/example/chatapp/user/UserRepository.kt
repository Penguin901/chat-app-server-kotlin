package com.example.chatapp.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): User?

    // 친구 추가 전 해당 이메일의 사용자 있는지 조회
    fun findByEmailAndDeletedFalseAndIdNot(email: String, userid: Long): User?

    // 친구 추가 전 해당 계정아이디의 사용자 있는지 조회
    fun findByUsernameAndDeletedFalseAndIdNot(username: String, userid: Long): User?

    // 계정 아이디 조회 (계정 아이디 추가전 중복검사)
    fun existsByUsernameAndIdNot(username: String, userid: Long): Boolean

}

