package com.flutterkada.interview.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flutterkada.interview.core.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI State - StateFlow for continuous state
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    // Events - SharedFlow for one-time events
    private val _event = MutableSharedFlow<LoginEvent>()
    val event = _event.asSharedFlow()

    /**
     * Single entry point for all user actions
     */
    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.LoginStarted -> {
                _state.update { it.copy(isLoading = true) }
            }

            is LoginAction.LoginSuccess -> {
                viewModelScope.launch {
                    try {
                        authRepository.saveAuthToken(action.token)
                        authRepository.saveUserEmail(action.email)
                        _event.emit(LoginEvent.NavigateToHome)
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false) }
                        _event.emit(LoginEvent.ShowError("Failed to save credentials"))
                    }
                }
            }

            is LoginAction.LoginFailed -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = false) }
                    _event.emit(LoginEvent.ShowError(action.message))
                }
            }

            is LoginAction.LoginCancelled -> {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
