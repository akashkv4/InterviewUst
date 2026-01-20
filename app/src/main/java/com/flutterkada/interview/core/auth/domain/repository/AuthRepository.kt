package com.flutterkada.interview.core.auth.domain.repository

interface AuthRepository {
    suspend fun saveAuthToken(token: String)
    suspend fun saveUserEmail(email: String)
    suspend fun getAuthToken(): String?
    suspend fun getUserEmail(): String?
    suspend fun clearAuthToken()
    suspend fun isAuthenticated(): Boolean
}
