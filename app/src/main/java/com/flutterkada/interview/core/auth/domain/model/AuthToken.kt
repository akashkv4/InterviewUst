package com.flutterkada.interview.core.auth.domain.model

data class AuthToken(
    val token: String,
    val expiresAt: Long? = null
)
