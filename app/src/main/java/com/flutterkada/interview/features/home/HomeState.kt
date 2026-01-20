package com.flutterkada.interview.features.home

import com.flutterkada.interview.core.device.domain.model.Device

/**
 * UI State for Home screen
 */
data class HomeState(
    val isDiscovering: Boolean = true,
    val devices: List<Device> = emptyList(),
    val isEmpty: Boolean = true
)
