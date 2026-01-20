package com.flutterkada.interview.features.login

/**
 * User actions/intents for Login screen
 * Single entry point via ViewModel.onAction()
 */
sealed class LoginAction {
    object LoginStarted : LoginAction()
    data class LoginSuccess(val token: String, val email: String) : LoginAction()
    data class LoginFailed(val message: String) : LoginAction()
    object LoginCancelled : LoginAction()
}
