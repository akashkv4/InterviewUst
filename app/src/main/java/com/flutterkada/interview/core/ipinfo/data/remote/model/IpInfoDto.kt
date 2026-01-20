package com.flutterkada.interview.core.ipinfo.data.remote.model

data class IpInfoDto(
    val ip: String,
    val city: String?,
    val region: String?,
    val country: String?,
    val org: String?
)
