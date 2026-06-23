package com.example.chatapp.user

import com.example.chatapp.security.UserPrincipal
import com.example.chatapp.user.dto.request.UpdateUserProfileRequest
import com.example.chatapp.user.dto.request.UpdateUsernameRequest
import com.example.chatapp.user.dto.response.UpdateUsernameResponse
import com.example.chatapp.user.dto.response.UserProfileResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "사용자 API")
class UserController(
    private val userService: UserService
) {
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필(닉네임, 상태메세지, 프로필이미지URL)을 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )
    @GetMapping("/me/profile")
    fun getMyProfile(@AuthenticationPrincipal userPrincipal: UserPrincipal): UserProfileResponse {
        val currentUserId = userPrincipal.userId
        return userService.getUserProfile(currentUserId)
    }

    @Operation(summary = "사용자 프로필 조회", description = "주어진 아이디(userId)의 프로필(닉네임, 상태메세지, 프로필이미지URL)을 조회합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )
    @GetMapping("/{userId}/profile")
    fun getUserProfile(@PathVariable userId: Long): UserProfileResponse {
        return userService.getUserProfile(userId)
    }

    @Operation(summary = "계정아이디(username) 사용 가능 여부 조회", description = "유효한 계정아이디(username)인지 검증하고, 중복 여부를 확인합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )
    @GetMapping("/username/availability")
    fun isUsernameAvailable(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestParam username: String
    ): Boolean {
        val currentUserId = userPrincipal.userId
        return userService.isUsernameAvailable(currentUserId, username)
    }

    @Operation(summary = "내 프로필 수정", description = "현재 로그인한 사용자의 프로필정보를 수정합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )
    @PutMapping("/me/profile")
    fun updateUserProfile(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: UpdateUserProfileRequest
    ): UserProfileResponse {
        val currentUserId = userPrincipal.userId
        return userService.updateUserProfile(currentUserId, request)
    }

    @Operation(summary = "내 계정아이디(username) 수정", description = "현재 로그인한 사용자의 계정아이디(username)를 수정합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = [Content()])
    )
    @PutMapping("/me/username")
    fun updateUsername(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: UpdateUsernameRequest
    ): UpdateUsernameResponse {
        val currentUserId = userPrincipal.userId
        return userService.updateUsername(currentUserId, request)
    }

    @Operation(summary = "회원탈퇴", description = "현재 로그인한 사용자의 계정을 비활성화합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "회원탈퇴 성공"),
        ApiResponse(responseCode = "401", description = "사용자 인증 실패")
    )
    @DeleteMapping("/me")
    fun deleteAccount(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<Void> {
        val currentUserId = userPrincipal.userId
        userService.deleteAccount(currentUserId)
        return ResponseEntity.noContent().build()
    }
}