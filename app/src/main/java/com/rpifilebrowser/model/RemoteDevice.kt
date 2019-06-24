package com.rpifilebrowser.model

import com.rpifilebrowser.R

class RemoteDevice(
    val name: String?,
    val address: String?,
    val rssi: Int?,
    val isConnectable: Boolean?) {

    fun signalStrengthIcon() =
        when (rssi) {
            in -60..0 -> R.drawable.ic_signal_4
            in -71..-61 -> R.drawable.ic_signal_3
            in -90..-71 -> R.drawable.ic_signal_2
            else -> R.drawable.ic_signal_1
        }
}