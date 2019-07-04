package com.rpifilebrowser.ui.devicebrowser.tools

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test

class BrowserTest {

    private val outputParser = OutputParser()

    @Test
    fun loadInitialDir() {
        // Given
        val browser = Browser(outputParser)
        val callback = mock<(String) -> Unit>()
        browser.commandInvoker = callback

        // When
        browser.loadInitialDir()

        // Then
        verify(callback)("ls -l /")
    }

    @Test
    fun open() {
        // Given
        val browser = Browser(outputParser)
        val callback = mock<(String) -> Unit>()
        browser.commandInvoker = callback

        // When
        with(browser) {
            loadInitialDir()
            open("home")
            open("pi")
        }

        // Then
        verify(callback)("ls -l /")
        verify(callback)("ls -l /home/")
        verify(callback)("ls -l /home/pi/")
    }

    @Test
    fun levelUp() {
        // Given
        val browser = Browser(outputParser)
        val callback = mock<(String) -> Unit>()
        browser.commandInvoker = callback

        // When
        with(browser) {
            loadInitialDir()
            open("home")
        }

        // Then
        verify(callback)("ls -l /")
        verify(callback)("ls -l /home/")
        verify(callback)("ls -l /")
    }

    @Test
    fun goToRoot() {
        // Given
        val browser = Browser(outputParser)
        val callback = mock<(String) -> Unit>()
        browser.commandInvoker = callback

        // When
        with(browser) {
            loadInitialDir()
            open("home")
            open("pi")
            goToRoot()
        }

        // Then
        verify(callback)("ls -l /")
        verify(callback)("ls -l /home/")
        verify(callback)("ls -l /home/pi/")
        verify(callback)("ls -l /")
    }
}