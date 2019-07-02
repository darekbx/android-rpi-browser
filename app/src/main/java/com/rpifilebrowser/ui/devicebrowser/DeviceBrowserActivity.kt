package com.rpifilebrowser.ui.devicebrowser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.R
import com.rpifilebrowser.ui.devicebrowser.tools.Browser
import com.rpifilebrowser.ui.deviceselect.DeviceSelectActivity.Companion.DEVICE_ADDRESS_KEY
import com.rpifilebrowser.viewmodels.DeviceCommandViewModel
import javax.inject.Inject

class DeviceBrowserActivity : AppCompatActivity() {

    @Inject
    lateinit var browser: Browser

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    internal lateinit var deviceCommandViewModel: DeviceCommandViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_browser)
        (application as FileBrowserApplication).appComponent.inject(this)

        deviceCommandViewModel = ViewModelProviders.of(this, viewModelFactory)[DeviceCommandViewModel::class.java]
        with(deviceCommandViewModel) {
            status.observe(this@DeviceBrowserActivity, Observer { status -> })
            output.observe(this@DeviceBrowserActivity, Observer { output -> })
            connect(getDeviceAddress())
        }
    }

    private fun getDeviceAddress() = intent.getStringExtra(DEVICE_ADDRESS_KEY)
}