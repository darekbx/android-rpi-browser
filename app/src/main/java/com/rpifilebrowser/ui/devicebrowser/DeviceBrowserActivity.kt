package com.rpifilebrowser.ui.devicebrowser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.R
import com.rpifilebrowser.bluetooth.BaseBluetooth.Companion.STATUS_SUCCESS
import com.rpifilebrowser.bluetooth.BluetoothCommand
import kotlinx.android.synthetic.main.activity_browser.*
import javax.inject.Inject

class DeviceBrowserActivity : AppCompatActivity() {

    companion object {
        val DEVICE_ADDRESS_KEY = "device_address"
    }

    @Inject
    lateinit var bluetoothCommand: BluetoothCommand

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        (application as FileBrowserApplication).appComponent.inject(this)



        bluetoothCommand.connect(getDeviceAddress())
        bluetoothCommand.onStatus = object: (Int) -> Unit {
            override fun invoke(status: Int) {
                if (status == STATUS_SUCCESS) {
                    // TODO

                }
            }

        }

        test_button.setOnClickListener {
            bluetoothCommand.executeCommand("test1 test2 test3 test4 test5 test6 test7 test8 test9 test10")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothCommand.dispose()
    }

    private fun getDeviceAddress() = intent.getStringExtra(DEVICE_ADDRESS_KEY)
}