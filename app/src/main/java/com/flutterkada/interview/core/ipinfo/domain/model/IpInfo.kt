package com.flutterkada.interview.core.ipinfo.domain.model

data class IpInfo(
    val ip: String,
    val city: String,
    val region: String,
    val country: String,
    val organization: String
)
