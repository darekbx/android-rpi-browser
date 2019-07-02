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
import com.rpifilebrowser.ui.deviceselect.DeviceSelectActivity.Companion.DEVICE_ADDRESS_KEY
import com.rpifilebrowser.viewmodels.DeviceCommandViewModel
import kotlinx.android.synthetic.main.activity_device_ssh.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceSSHActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    internal lateinit var deviceCommandViewModel: DeviceCommandViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_ssh)
        (application as FileBrowserApplication).appComponent.inject(this)

        deviceCommandViewModel = ViewModelProviders.of(this, viewModelFactory)[DeviceCommandViewModel::class.java]
        with(deviceCommandViewModel) {
            status.observe(this@DeviceSSHActivity, Observer { status -> updateButtonStatus(status) })
            output.observe(this@DeviceSSHActivity, Observer { output -> updateOutput(output) })
            connect(getDeviceAddress())
        }
    }

    fun onAction(v: View) {
        var command = action_box.text.toString()
        when (TextUtils.isEmpty(command)) {
            true -> action_box.error = "Please enter command"
            else -> deviceCommandViewModel.executeCommand(command)
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