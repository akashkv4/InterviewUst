package com.flutterkada.interview.features.home

import com.flutterkada.interview.core.device.domain.model.Device

/**
 * One-time events for Home screen
 */
sealed class HomeEvent {
    data class NavigateToDetail(val device: Device) : HomeEvent()
    data class ShowMessage(val message: String) : HomeEvent()
}
