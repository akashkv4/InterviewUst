package com.flutterkada.interview.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flutterkada.interview.core.device.data.local.DeviceDao
import com.flutterkada.interview.core.device.data.local.DeviceEntity

@Database(
    entities = [DeviceEntity::class],
    version = 2,  // Bumped: Changed primary key from name to ipAddress
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}
