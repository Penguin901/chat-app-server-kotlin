package com.example.chatapp.security

import org.springframework.security.core.GrantedAuthority

data class UserPrincipal(
    val userId: Long,
    val authorities: Collection<GrantedAuthority>
)