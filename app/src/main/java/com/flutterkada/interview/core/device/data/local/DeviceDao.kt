package com.flutterkada.interview.core.device.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Upsert
    suspend fun upsertDevice(device: DeviceEntity)

    @Query("SELECT * FROM discovered_devices ORDER BY isOnline DESC, name ASC")
    fun getAllDevices(): Flow<List<DeviceEntity>>

    @Query("UPDATE discovered_devices SET isOnline = 0")
    suspend fun markAllOffline()

    @Query("DELETE FROM discovered_devices")
    suspend fun clearAllDevices()
}
