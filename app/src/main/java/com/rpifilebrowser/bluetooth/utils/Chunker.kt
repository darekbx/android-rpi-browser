package com.rpifilebrowser.bluetooth.utils

object Chunker {

    private val PACKAGE_START_HEADER = "---start-"
    private val PACKAGE_END_HEADER = "---end-"
    private val PACKAGE_SIZE = 20

    fun commandToChunks(command: String): List<String> {
        val commandLength = command.length
        return when (commandLength > PACKAGE_SIZE) {
            true -> command.chunkedSequence(PACKAGE_SIZE).toMutableList()
            else -> mutableListOf(command)
        }.apply {
            add(0, "$PACKAGE_START_HEADER$commandLength")
            add("$PACKAGE_END_HEADER$commandLength")
        }
    }
}