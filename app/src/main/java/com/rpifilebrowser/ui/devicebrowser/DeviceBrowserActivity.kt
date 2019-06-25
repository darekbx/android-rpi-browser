package com.rpifilebrowser.ui.devicebrowser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.bluetooth.BluetoothCommand
import javax.inject.Inject

class DeviceBrowserActivity : AppCompatActivity() {

    companion object {
        val DEVICE_ADDRESS_KEY = "device_address"
    }

    @Inject
    lateinit var bluetoothCommand: BluetoothCommand

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set content view
        (application as FileBrowserApplication).appComponent.inject(this)


        bluetoothCommand.connect(getDeviceAddress())
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothCommand.dispose()
    }

    private fun getDeviceAddress() = intent.getStringExtra(DEVICE_ADDRESS_KEY)
}