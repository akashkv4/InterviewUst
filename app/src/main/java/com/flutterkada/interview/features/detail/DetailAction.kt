package com.flutterkada.interview.features.detail

/**
 * User actions for Detail screen
 */
sealed class DetailAction {
    data class LoadIpInfo(val deviceName: String, val deviceIp: String) : DetailAction()
    object Retry : DetailAction()
}
