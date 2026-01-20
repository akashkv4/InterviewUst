package com.flutterkada.interview.core.auth.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val USER_EMAIL = stringPreferencesKey("user_email")
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN] = token }
    }

    suspend fun getAuthToken(): String? {
        return dataStore.data.firstOrNull()?.get(AUTH_TOKEN)
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { it[USER_EMAIL] = email }
    }

    suspend fun getUserEmail(): String? {
        return dataStore.data.firstOrNull()?.get(USER_EMAIL)
    }

    suspend fun clearAuthToken() {
        dataStore.edit { 
            it.remove(AUTH_TOKEN)
            it.remove(USER_EMAIL)
        }
    }
}
