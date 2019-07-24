package com.rpifilebrowser.ui.devicebrowser.tools

import org.junit.Test

import org.junit.Assert.*

class OutputParserTest {

    private val sample =
        """total 10
-rw-r--r--  1 mac  admin   891 Jun 17 21:26 RPiFileBrowser.iml
drwxr-xr-x  9 mac  admin   288 Jul  2 22:04 app
drwxr-xr-x  3 mac  admin    96 Jun 17 21:35 build
-rw-r--r--  1 mac  admin   694 Jun 28 21:38 build.gradle
-rwxr-xr-x  1 mac  admin  1260 Mar 17 20:52 common_debug.keystore
drwxr-xr-x  3 mac  admin    96 Jun 17 21:17 gradle
-rw-r--r--  1 mac  admin  1163 Jun 17 21:18 gradle.properties
-rwxr--r--  1 mac  admin  5296 Jun 17 21:17 gradlew
-rw-r--r--  1 mac  admin  2260 Jun 17 21:17 gradlew.bat
-rw-r--r--  1 mac  admin   428 Jun 17 21:17 local.properties
-rw-r--r--  1 mac  admin    15 Jun 17 21:17 settings.gradle
"""

    @Test
    fun parse() {
        // Given
        val parser = OutputParser()

        // When
        val entries = parser.parse(sample)

        // Then
        assertEquals(11, entries.size)

        with(entries[0]) {
            assertEquals("RPiFileBrowser.iml", name)
            assertEquals(false, isDirectory)
            assertEquals("Jun 17 21:26", date)
            assertEquals(891, size)
        }

        with(entries[1]) {
            assertEquals("app", name)
            assertEquals(true, isDirectory)
            assertEquals("Jul 2 22:04", date)
        }
    }
}