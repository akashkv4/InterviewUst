package com.flutterkada.interview.features.detail

import com.flutterkada.interview.core.ipinfo.domain.model.IpInfo

/**
 * UI State for Detail screen
 */
data class DetailState(
    val isLoading: Boolean = true,
    val ipInfo: IpInfo? = null,
    val error: String? = null,
    val deviceName: String = "",
    val deviceIp: String = ""
)
