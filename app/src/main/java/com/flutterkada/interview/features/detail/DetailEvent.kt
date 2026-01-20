package com.flutterkada.interview.features.detail

/**
 * One-time events for Detail screen
 */
sealed class DetailEvent {
    data class ShowError(val message: String) : DetailEvent()
}
