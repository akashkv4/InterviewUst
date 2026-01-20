package com.flutterkada.interview.features.home

import com.flutterkada.interview.core.device.domain.model.Device

/**
 * User actions for Home screen
 */
sealed class HomeAction {
    object StartDiscovery : HomeAction()
    object StopDiscovery : HomeAction()
    object RefreshDevices : HomeAction()
    data class DeviceClicked(val device: Device) : HomeAction()
}
