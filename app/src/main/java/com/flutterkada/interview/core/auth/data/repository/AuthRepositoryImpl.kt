package com.flutterkada.interview.core.auth.data.repository

import com.flutterkada.interview.core.auth.data.local.SessionManager
import com.flutterkada.interview.core.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun saveAuthToken(token: String) {
        sessionManager.saveAuthToken(token)
    }

    override suspend fun saveUserEmail(email: String) {
        sessionManager.saveUserEmail(email)
    }

    override suspend fun getAuthToken(): String? {
        return sessionManager.getAuthToken()
    }

    override suspend fun getUserEmail(): String? {
        return sessionManager.getUserEmail()
    }

    override suspend fun clearAuthToken() {
        sessionManager.clearAuthToken()
    }

    override suspend fun isAuthenticated(): Boolean {
        val token = sessionManager.getAuthToken()
        return !token.isNullOrEmpty()
    }
}
