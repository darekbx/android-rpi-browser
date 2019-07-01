package com.rpifilebrowser.ui.devicessh

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.R
import com.rpifilebrowser.bluetooth.BaseBluetooth
import kotlinx.android.synthetic.main.activity_browser.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceBrowserActivity : AppCompatActivity() {

    companion object {
        val DEVICE_ADDRESS_KEY = "device_address"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    internal lateinit var deviceSSHViewModel: DeviceSSHViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        (application as FileBrowserApplication).appComponent.inject(this)

        deviceSSHViewModel = ViewModelProviders.of(this, viewModelFactory)[DeviceSSHViewModel::class.java]
        with(deviceSSHViewModel) {
            status.observe(this@DeviceBrowserActivity, Observer { status -> updateButtonStatus(status) })
            output.observe(this@DeviceBrowserActivity, Observer { output -> updateOutput(output) })
            connect(getDeviceAddress())
        }
    }

    fun onAction(v: View) {
        var command = action_box.text.toString()
        when (TextUtils.isEmpty(command)) {
            true -> action_box.error = "Please enter command"
            else -> deviceSSHViewModel.executeCommand(command)
        }
    }

    private fun updateButtonStatus(status: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            when (status) {
                BaseBluetooth.STATUS_SUCCESS -> test_button.isEnabled = true
                BaseBluetooth.ERROR_DISCONNECTED -> test_button.isEnabled = false
            }
        }
    }

    private fun updateOutput(result: String) {
        CoroutineScope(Dispatchers.Main).launch {
            output.setText(result)
        }
    }

    private fun getDeviceAddress() = intent.getStringExtra(DEVICE_ADDRESS_KEY)
}