package com.flutterkada.interview.core.device.data.repository

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.flutterkada.interview.core.device.data.local.DeviceDao
import com.flutterkada.interview.core.device.data.local.DeviceEntity
import com.flutterkada.interview.core.device.data.local.toDomain
import com.flutterkada.interview.core.device.domain.model.Device
import com.flutterkada.interview.core.device.domain.repository.DeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao,
    @ApplicationContext private val context: Context
) : DeviceRepository {

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * List of common mDNS service types to discover
     * These cover most home network devices
     */
    private val serviceTypes = listOf(
        "_airplay._tcp.",      // Apple AirPlay (Mac, Apple TV, HomePod)
        "_raop._tcp.",         // Remote Audio Output Protocol (AirPlay audio)
        "_googlecast._tcp.",   // Google Chromecast, Google Home
        "_ipp._tcp.",          // Internet Printing Protocol (Printers)
        "_printer._tcp.",      // Network Printers
        "_scanner._tcp.",      // Network Scanners
        "_http._tcp.",         // HTTP servers (many smart devices)
        "_hap._tcp.",          // HomeKit Accessory Protocol
        "_smb._tcp.",          // SMB file sharing (NAS, Windows)
        "_afpovertcp._tcp.",   // Apple File Protocol (Mac file sharing)
        "_spotify-connect._tcp.", // Spotify Connect devices
        "_sonos._tcp.",        // Sonos speakers
        "_roku._tcp.",         // Roku devices
    )

    // Keep track of active discovery listeners
    private val activeListeners = mutableMapOf<String, NsdManager.DiscoveryListener>()

    override fun getAllDevices(): Flow<List<Device>> {
        return deviceDao.getAllDevices().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun startDiscovery() {
        deviceDao.markAllOffline()

        // Start discovery for each service type
        serviceTypes.forEach { serviceType ->
            try {
                val listener = createDiscoveryListener(serviceType)
                activeListeners[serviceType] = listener
                nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
                Log.d(TAG, "Started discovery for: $serviceType")
            } catch (e: Exception) {
                Log.e(TAG, "Error starting discovery for $serviceType", e)
            }
        }
    }

    override fun stopDiscovery() {
        // Stop all active discoveries
        activeListeners.forEach { (serviceType, listener) ->
            try {
                nsdManager.stopServiceDiscovery(listener)
                Log.d(TAG, "Stopped discovery for: $serviceType")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping discovery for $serviceType", e)
            }
        }
        activeListeners.clear()
    }

    private fun createDiscoveryListener(serviceType: String) = object : NsdManager.DiscoveryListener {
        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            Log.d(TAG, "Service found: ${serviceInfo.serviceName} (type: $serviceType)")
            nsdManager.resolveService(serviceInfo, createResolveListener())
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            Log.d(TAG, "Service lost: ${serviceInfo.serviceName}")
            repositoryScope.launch {
                deviceDao.upsertDevice(
                    DeviceEntity(
                        name = serviceInfo.serviceName,
                        ipAddress = "",
                        isOnline = false
                    )
                )
            }
        }

        override fun onDiscoveryStarted(regType: String) {
            Log.d(TAG, "Discovery started for type: $regType")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.d(TAG, "Discovery stopped for type: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Start discovery failed for $serviceType: $errorCode")
            activeListeners.remove(serviceType)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Stop discovery failed for $serviceType: $errorCode")
        }
    }

    private fun createResolveListener() = object : NsdManager.ResolveListener {
        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            val ip = serviceInfo.host?.hostAddress ?: return // Skip if no IP
            val cleanName = cleanDeviceName(serviceInfo.serviceName)

            Log.d(TAG, "Service resolved: $cleanName at $ip")
            val device = DeviceEntity(
                ipAddress = ip,
                name = cleanName,
                isOnline = true
            )
            repositoryScope.launch {
                deviceDao.upsertDevice(device)
            }
        }

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            Log.e(TAG, "Resolve failed for ${serviceInfo.serviceName}: $errorCode")
        }
    }


    private fun cleanDeviceName(name: String): String {
        // Pattern: MAC_ADDRESS@ prefix (used by _raop._tcp and others)
        val macPrefixPattern = Regex("^[0-9A-Fa-f]{12}@")
        return name.replace(macPrefixPattern, "")
    }

    companion object {
        private const val TAG = "DeviceRepository"
    }
}
