package com.flutterkada.interview.core.device.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discovered_devices")
data class DeviceEntity(
    @PrimaryKey
    val ipAddress: String,
    val name: String,
    val isOnline: Boolean
)
