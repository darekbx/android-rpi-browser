package com.rpifilebrowser.ui.devicebrowser.tools

import com.rpifilebrowser.model.BrowserItem

class OutputParser {

    fun parse(output: String) = output
            .lines()
            .map { itemFromLine(it) }

    private fun itemFromLine(line: String): BrowserItem {
        var isDirectory = line[0] == 'd'
        val chunks = line.split("\\s+".toRegex())
        var date = (5..7).map { chunks[it] }.joinToString(" ")
        return BrowserItem(chunks.last(), isDirectory, chunks[4].toLong(), date)
    }
}