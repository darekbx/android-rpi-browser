package com.rpifilebrowser.bluetooth.utils

object Merger {

    private val PACKAGE_START_HEADER = "---start-"
    private val PACKAGE_END_HEADER = "---end-"

    private var hasIncomingPacket = false
    private var packetList = mutableListOf<String>()
    private var size: Int = 0
    private var sum: Int = 0

    var onProgress: ((progress: Int, max: Int) -> Unit)? = null

    fun obtainPacket(packet: String, onComplete: (mergedOutput: String) -> Unit) {
        when {
            packet.startsWith(PACKAGE_START_HEADER) -> {
                packet
                    .removeSuffix(PACKAGE_START_HEADER)
                    .toIntOrNull()
                    ?.let {
                    size = it
                }
                hasIncomingPacket = true
                packetList.clear()
            }
            packet.startsWith(PACKAGE_END_HEADER) -> {
                hasIncomingPacket = false
                var result = packetList.joinToString("")
                onComplete(result)
            }
            hasIncomingPacket == true -> {
                packetList.add(packet)
                sum += packet.length
                onProgress?.invoke(size, sum)
            }
        }
    }
}