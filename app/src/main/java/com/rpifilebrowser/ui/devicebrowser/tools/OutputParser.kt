package com.rpifilebrowser.ui.devicebrowser.tools

import com.rpifilebrowser.model.BrowserItem

class OutputParser {

    fun parse(output: String) = output
        .lines()
        .filter { verifyLine(it) }
        .map { itemFromLine(it) }
        .sortedByDescending { it.isDirectory }

    private fun verifyLine(it: String) = it != null && it.length > 0 && it.startsWith("total").not()

    private fun itemFromLine(line: String): BrowserItem {
        var isDirectory = line[0] == 'd'
        val chunks = line.split("\\s+".toRegex())
        var date = (5..7).map { chunks[it] }.joinToString(" ")
        return BrowserItem(chunks.last(), isDirectory, chunks[4].toLong(), date)
    }
}