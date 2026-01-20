package com.flutterkada.interview.features.login

/**
 * One-time events for Login screen
 * Represented as SharedFlow in ViewModel
 */
sealed class LoginEvent {
    object NavigateToHome : LoginEvent()
    data class ShowError(val message: String) : LoginEvent()
}
