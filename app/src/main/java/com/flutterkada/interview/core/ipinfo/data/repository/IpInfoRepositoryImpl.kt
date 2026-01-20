package com.flutterkada.interview.core.ipinfo.data.repository

import com.flutterkada.interview.core.ipinfo.data.remote.NetworkClient
import com.flutterkada.interview.core.ipinfo.data.remote.model.IpInfoDto
import com.flutterkada.interview.core.ipinfo.data.remote.model.toDomain
import com.flutterkada.interview.core.ipinfo.domain.model.IpInfo
import com.flutterkada.interview.core.ipinfo.domain.repository.IpInfoRepository
import com.flutterkada.interview.core.util.Resource
import org.json.JSONObject
import javax.inject.Inject

class IpInfoRepositoryImpl @Inject constructor(
    private val networkClient: NetworkClient
) : IpInfoRepository {

    override suspend fun getPublicIpDetails(): Resource<IpInfo> {
        return try {
            // Step 1: Get Public IP from ipify API
            // The networkClient is now main-safe (suspend function)
            val ipifyResponse = networkClient.executeGetRequest(IPIFY_URL)
            val publicIp = ipifyResponse.getOrNull()?.let {
                parseIpifyResponse(it)
            } ?: return Resource.Error(
                "Could not fetch public IP.",
                ipifyResponse.exceptionOrNull()
            )

            // Step 2: Get IP Geo Info from ipinfo.io
            val ipInfoResponse = networkClient.executeGetRequest("$IPINFO_BASE_URL/$publicIp/geo")
            ipInfoResponse.fold(
                onSuccess = { responseString ->
                    val ipInfoDto = parseIpInfoResponse(responseString, publicIp)
                    Resource.Success(ipInfoDto.toDomain())
                },
                onFailure = { exception ->
                    Resource.Error("Could not fetch IP details.", exception)
                }

            )
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred.", e)
        }
    }

    private fun parseIpifyResponse(jsonString: String): String {
        val json = JSONObject(jsonString)
        return json.getString("ip")
    }

    private fun parseIpInfoResponse(jsonString: String, ip: String): IpInfoDto {
        val json = JSONObject(jsonString)
        return IpInfoDto(
            ip = ip,
            city = json.optString("city", null),
            region = json.optString("region", null),
            country = json.optString("country", null),
            org = json.optString("org", null)
        )
    }

    companion object {
        private const val IPIFY_URL = "https://api.ipify.org?format=json"
        private const val IPINFO_BASE_URL = "https://ipinfo.io"
    }
}
