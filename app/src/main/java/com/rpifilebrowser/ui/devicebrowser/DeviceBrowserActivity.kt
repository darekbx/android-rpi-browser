package com.rpifilebrowser.ui.devicebrowser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.R
import com.rpifilebrowser.bluetooth.BaseBluetooth
import com.rpifilebrowser.ui.devicebrowser.tools.Browser
import com.rpifilebrowser.ui.deviceselect.DeviceSelectActivity.Companion.DEVICE_ADDRESS_KEY
import com.rpifilebrowser.viewmodels.DeviceCommandViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

class DeviceBrowserActivity : AppCompatActivity() {

    companion object {
        val DELAY = 250L
    }

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
            status.observe(this@DeviceBrowserActivity, Observer { status -> handleStatus(status) })
            output.observe(this@DeviceBrowserActivity, Observer { output -> browser.parseResult(output) })
            connect(getDeviceAddress())
        }

        browser.commandInvoker = { command -> deviceCommandViewModel.executeCommand(command) }
        browser.browserItems = { items -> /* TODO */}
    }

    private fun handleStatus(status: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            when (status) {
                BaseBluetooth.STATUS_SUCCESS -> loadRoot()
                BaseBluetooth.ERROR_DISCONNECTED -> {
                    // TODO
                }
            }
        }
    }

    private fun loadRoot() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(DELAY)
            CoroutineScope(Dispatchers.Main).launch {
                browser.loadInitialDir()
            }
        }
    }

    private fun getDeviceAddress() = intent.getStringExtra(DEVICE_ADDRESS_KEY)
}