package com.flutterkada.interview.core.ipinfo.data.remote.model

import com.flutterkada.interview.core.ipinfo.domain.model.IpInfo

fun IpInfoDto.toDomain(): IpInfo {
    return IpInfo(
        ip = this.ip,
        city = this.city ?: "Unknown",
        region = this.region ?: "Unknown",
        country = this.country ?: "Unknown",
        organization = this.org ?: "Unknown"
    )
}
