package com.flutterkada.interview.core.device.domain.repository

import com.flutterkada.interview.core.device.domain.model.Device
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getAllDevices(): Flow<List<Device>>
    suspend fun startDiscovery()
    fun stopDiscovery()
}
