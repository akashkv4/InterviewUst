package com.flutterkada.interview.core.ipinfo.domain.repository

import com.flutterkada.interview.core.ipinfo.domain.model.IpInfo
import com.flutterkada.interview.core.util.Resource

interface IpInfoRepository {
    suspend fun getPublicIpDetails(): Resource<IpInfo>
}
