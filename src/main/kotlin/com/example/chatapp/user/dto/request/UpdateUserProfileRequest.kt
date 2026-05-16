package com.example.chatapp.user.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateUserProfileRequest(
    @field:NotBlank
    val nickname: String,

    @field:Size(max = 255)
    val bio: String?,

    val profileImageUrl: String?

)