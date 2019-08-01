package com.rpifilebrowser.ui.devicebrowser

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.R
import com.rpifilebrowser.bluetooth.BaseBluetooth
import com.rpifilebrowser.model.BrowserItem
import com.rpifilebrowser.ui.devicebrowser.tools.Browser
import com.rpifilebrowser.ui.deviceselect.DeviceSelectActivity.Companion.DEVICE_ADDRESS_KEY
import com.rpifilebrowser.viewmodels.DeviceCommandViewModel
import kotlinx.android.synthetic.main.activity_device_browser.*
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
            fileOutput.observe(this@DeviceBrowserActivity, Observer { fileOutput -> handleFile(fileOutput) })
            progress.observe(this@DeviceBrowserActivity, Observer { handleProgress(it.first, it.second) })
            connect(getDeviceAddress())
        }

        browser.commandInvoker = { command -> deviceCommandViewModel.executeCommand(command) }
        browser.browserItems = { items ->
            browserAdapter.clear()
            browserAdapter.addAll(items)
            hideProgress()
        }

        browser_list.adapter = browserAdapter

        browserAdapter.onItemClick = { item ->
            showProgress()
            when (item.isDirectory) {
                true -> browser.open(item.name)
                else -> openFile(item)
            }
        }

        showProgress()
    }

    fun onGoUpClick(v: View) {
        if (browser.canGoUp()) {
            showProgress()
            browser.levelUp()
        }
    }

    private fun openFile(item: BrowserItem) {
        val path = "${browser.getPath()}${item.name}"
        deviceCommandViewModel.readFile(path)
    }

    private fun handleProgress(progress: Int, max: Int) {
        progress_circular.max = max
        progress_circular.progress = progress
    }

    private fun handleFile(fileContents: String) {
        // TODO: ask what to do with file
    }

    private fun handleStatus(status: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            when (status) {
                BaseBluetooth.STATUS_SUCCESS -> loadRoot()
                BaseBluetooth.ERROR_DISCONNECTED -> {
                    hideProgress()
                    showDisconnectedDialog()
                }
            }
        }
    }

    private fun showDisconnectedDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage(R.string.device_disconnected)
            .setPositiveButton(R.string.close, { v, i -> finish() })
            .show()
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

    private val browserAdapter by lazy { BrowserAdapter(this) }

    private fun showProgress() {
        progress_container.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progress_container.visibility = View.GONE
    }
}