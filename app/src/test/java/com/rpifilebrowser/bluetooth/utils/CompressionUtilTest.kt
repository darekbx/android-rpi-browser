package com.rpifilebrowser.bluetooth.utils

import org.junit.Assert.*
import org.junit.Test

class CompressionUtilTest {

    @Test
    fun encodeAndDecode() {
        // Given
        val data = "Test data 1234"

        // When
        val encoded = CompressionUtil.encodeData(data)
        val decoded = CompressionUtil.decodeData(encoded)

        // Then
        assertEquals(data, decoded)
    }
}