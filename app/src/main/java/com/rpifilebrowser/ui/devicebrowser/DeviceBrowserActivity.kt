package com.rpifilebrowser.ui.devicebrowser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DeviceBrowserActivity : AppCompatActivity() {

    companion object {
        val DEVICE_ADDRESS_KEY = "device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        

    }

    private fun getDeviceAddress() = intent.getStringExtra(DEVICE_ADDRESS_KEY)
}