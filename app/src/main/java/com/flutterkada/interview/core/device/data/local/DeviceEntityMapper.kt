package com.flutterkada.interview.core.device.data.local

import com.flutterkada.interview.core.device.domain.model.Device

fun DeviceEntity.toDomain(): Device {
    return Device(
        name = this.name,
        ipAddress = this.ipAddress,
        isOnline = this.isOnline
    )
}

fun Device.toEntity(): DeviceEntity {
    return DeviceEntity(
        name = this.name,
        ipAddress = this.ipAddress,
        isOnline = this.isOnline
    )
}
