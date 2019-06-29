package com.rpifilebrowser.ui.devicebrowser

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.R
import com.rpifilebrowser.bluetooth.BaseBluetooth
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
                runOnUiThread {
                    when (status) {
                        BaseBluetooth.STATUS_SUCCESS -> test_button.isEnabled = true
                        BaseBluetooth.ERROR_DISCONNECTED -> test_button.isEnabled = false
                    }
                }
            }
        }
        bluetoothCommand.onCommandResult = object: (String) -> Unit {
            override fun invoke(result: String) {
                runOnUiThread {
                    output.setText(result)
                }
            }
        }

        test_button.setOnClickListener {
            var command = action_box.text.toString()
            when (TextUtils.isEmpty(command)) {
                true -> action_box.error = "Please enter command"
                else -> bluetoothCommand.executeCommand(command)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothCommand.dispose()
    }

    private fun getDeviceAddress() = intent.getStringExtra(DEVICE_ADDRESS_KEY)
}