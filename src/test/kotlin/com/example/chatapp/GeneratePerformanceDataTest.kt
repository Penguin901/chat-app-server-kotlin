package com.example.chatapp

import com.example.chatapp.auth.oauth.OAuthProvider
import com.example.chatapp.chat.room.ChatRoomService
import com.example.chatapp.security.jwt.JwtService
import com.example.chatapp.user.User
import com.example.chatapp.user.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.io.File

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class GeneratePerformanceDataTest(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val chatRoomService: ChatRoomService,
) {
    @Test
    fun generatePerformanceTestData() {

        // 유저 생성 (101명)
        val users = (1..101).map {
            User.create(
                email = "loadtest-user$it@test.com",
                oauthId = "loadtest-user$it,",
                provider = OAuthProvider.GOOGLE
            )
        }

        val savedUsers = userRepository.saveAll(users)

        savedUsers.forEach { user ->
            user.updateUserProfile("${user.id}user", "", "")
        }

        userRepository.saveAll(savedUsers)

        // 채팅방 생성 (100개)
        val firstUser = savedUsers.first()

        val chatRooms = savedUsers.drop(1).map { otherUser ->
            var directRoomKey = chatRoomService.generateDirectRoomKey(
                firstUser.id!!,
                otherUser.id!!
            )

            chatRoomService.createDirectChatRoom(
                directRoomKey.also { directRoomKey = it },
                participants = listOf(firstUser, otherUser)
            )
        }

        // accessToken 생성
        val tokens = savedUsers.associate { user ->
            user.id!! to jwtService.createTokens(user.id!!).accessToken
        }

        val dataList = savedUsers
            .drop(1)
            .zip(chatRooms)
            .map { (user, chatRoom) ->
                "${user.id},${chatRoom.id},${tokens[user.id!!]}"
            }

        File("test_data.csv").writeText(
            "userId,chatRoomId,accessToken\n" + dataList.joinToString("\n")
        )

    }

}
