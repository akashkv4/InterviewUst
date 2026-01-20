package com.flutterkada.interview.features.splash

/**
 * One-time events for Splash screen
 */
sealed class SplashEvent {
    object NavigateToHome : SplashEvent()
    object NavigateToLogin : SplashEvent()
}
