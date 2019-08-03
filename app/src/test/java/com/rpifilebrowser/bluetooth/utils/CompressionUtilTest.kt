package com.rpifilebrowser.bluetooth.utils

import org.junit.Assert.*
import org.junit.Test

class CompressionUtilTest {

    @Test
    fun encodeAndDecode() {
        // Given
        val decoder =  CompressionUtil()
        val data = "Test data 1234"

        // When
        val encoded = decoder.encodeData(data)
        val decoded = decoder.decodeData(encoded)

        // Then
        assertEquals(data, decoded)
    }
}