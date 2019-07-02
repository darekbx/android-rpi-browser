package com.rpifilebrowser.ui.devicebrowser.tools

import com.rpifilebrowser.model.BrowserItem

class Browser(val outputParser: OutputParser) {

    private var path = mutableListOf("/")

    var commandInvoker: ((command: String) -> String)? = null
    var browserItems: ((items: List<BrowserItem) -> Unit)? = null

    fun loadInitialDir() {
        execute()
    }

    fun open(directory: String) {
        path.add("$directory/")
        execute()
    }

    fun levelUp() {
        path.removeAt(path.size - 1)
        execute()
    }

    fun goToRoot() {
        path.clear()
        path.add("/")
        execute()
    }

    private fun execute() {
        val pathParts = path.joinToString("")
        val command = "ls -l $pathParts"
        val result = commandInvoker?.invoke(command)
        result?.let { result ->
            var items = outputParser.parse(result)
            browserItems?.invoke(items)
        }
    }
}