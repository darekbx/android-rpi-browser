package com.rpifilebrowser.bluetooth.utils

object Merger {

    private val PACKAGE_START_HEADER = "---start-"
    private val PACKAGE_END_HEADER = "---end-"

    private var hasIncomingPacket = false
    private var packetList = mutableListOf<String>()

    fun obtainPacket(packet: String, onComplete: (mergedOutput: String) -> Unit) {
        when {
            packet.startsWith(PACKAGE_START_HEADER) -> {
                hasIncomingPacket = true
                packetList.clear()
            }
            packet.startsWith(PACKAGE_END_HEADER) -> {
                hasIncomingPacket = false
                var result = packetList.joinToString("")
                onComplete(result)
            }
            hasIncomingPacket == true -> packetList.add(packet)
        }
    }
}