package com.example.chatapp.user

import com.example.chatapp.security.UserPrincipal
import com.example.chatapp.user.dto.request.UpdateUserProfileRequest
import com.example.chatapp.user.dto.request.UpdateUsernameRequest
import com.example.chatapp.user.dto.response.UpdateUsernameResponse
import com.example.chatapp.user.dto.response.UserProfileResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/me/profile")
    fun getMyProfile(@AuthenticationPrincipal userPrincipal: UserPrincipal): UserProfileResponse {
        val currentUserId = userPrincipal.userId
        return userService.getUserProfile(currentUserId)
    }

    // 다른 유저 프로필 가져오기
    @GetMapping("/{userId}/profile")
    fun getUserProfile(@PathVariable userId: Long): UserProfileResponse {
        return userService.getUserProfile(userId)
    }

    // 사용자 아이디 중복 조회
    @GetMapping("/username/availability")
    fun isUsernameAvailable(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestParam username: String
    ): Boolean {
        val currentUserId = userPrincipal.userId
        return userService.isUsernameAvailable(currentUserId, username)
    }

    // 사용자 프로필 수정
    @PutMapping("/me/profile")
    fun updateUserProfile(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: UpdateUserProfileRequest
    ): UserProfileResponse {
        val currentUserId = userPrincipal.userId
        return userService.updateUserProfile(currentUserId, request)
    }

    // 사용자 아이디 수정
    @PutMapping("/me/username")
    fun updateUsername(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: UpdateUsernameRequest
    ): UpdateUsernameResponse {
        val currentUserId = userPrincipal.userId
        return userService.updateUsername(currentUserId, request)
    }

    @DeleteMapping("/me")
    fun deleteAccount(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<Void> {
        val currentUserId = userPrincipal.userId
        userService.deleteAccount(currentUserId)
        return ResponseEntity.noContent().build()
    }
}