package com.flutterkada.interview.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flutterkada.interview.core.auth.domain.repository.AuthRepository
import com.flutterkada.interview.core.util.NetworkChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    // UI State
    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    // Events
    private val _event = MutableSharedFlow<SplashEvent>()
    val event = _event.asSharedFlow()

    init {
        onAction(SplashAction.CheckAuthState)
    }

    /**
     * Single entry point for all user actions
     */
    fun onAction(action: SplashAction) {
        when (action) {
            is SplashAction.CheckAuthState -> {
                checkAuthenticationState()
            }
        }
    }

    private fun checkAuthenticationState() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val cachedToken = authRepository.getAuthToken()
            val hasToken = !cachedToken.isNullOrEmpty()

            if (hasToken) {
                // We have a stored token (requirement: "Cache the token")
                // Now check if we can do "silent authentication" (Requirement Part 1)

                if (networkChecker.isNetworkAvailable()) {
                    // Network reachable: Silent auth success -> Home
                    _event.emit(SplashEvent.NavigateToHome)
                } else {
                    // Network NOT reachable: Force logout (Requirement Part 1)
                    authRepository.clearAuthToken()
                    _event.emit(SplashEvent.NavigateToLogin)
                }
            } else {
                // No token -> Login
                _event.emit(SplashEvent.NavigateToLogin)
            }

            _state.update { it.copy(isLoading = false) }
        }
    }
}
