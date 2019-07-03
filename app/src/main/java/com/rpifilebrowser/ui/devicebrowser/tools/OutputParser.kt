package com.rpifilebrowser.ui.devicebrowser.tools

import com.rpifilebrowser.model.BrowserItem

class OutputParser {

    fun parse(output: String) = output
            .lines()
            .map { itemFromLine(it) }

    private fun itemFromLine(line: String): BrowserItem {
        return BrowserItem("", false, 0, "")
    }
}