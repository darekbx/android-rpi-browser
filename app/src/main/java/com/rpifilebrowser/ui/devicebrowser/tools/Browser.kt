package com.rpifilebrowser.ui.devicebrowser.tools

import com.rpifilebrowser.model.BrowserItem

class Browser(val outputParser: OutputParser) {

    private var path = mutableListOf("/")

    var commandInvoker: ((command: String) -> Unit)? = null
    var browserItems: ((items: List<BrowserItem>) -> Unit)? = null

    fun getPath() = path

    fun loadInitialDir() {
        execute()
    }

    fun open(directory: String) {
        path.add("$directory/")
        execute()
    }

    fun canGoUp() = path.size > 0

    fun levelUp() {
        if (path.size > 0) {
            path.removeAt(path.size - 1)
            execute()
        }
    }

    fun goToRoot() {
        path.clear()
        path.add("/")
        execute()
    }

    fun parseResult(result: String?) {
        result?.let { result ->
            var items = outputParser.parse(result)
            browserItems?.invoke(items)
        }
    }

    private fun execute() {
        val pathParts = path.joinToString("")
        val command = "ls -l $pathParts"
        commandInvoker?.invoke(command)
    }
}