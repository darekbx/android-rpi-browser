package com.rpifilebrowser.ui.devicebrowser

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
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
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
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

    private var openedItem: BrowserItem? = null
    private var downloadAction = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_browser)
        (application as FileBrowserApplication).appComponent.inject(this)

        deviceCommandViewModel = ViewModelProviders.of(this, viewModelFactory)[DeviceCommandViewModel::class.java]
        with(deviceCommandViewModel) {
            status.observe(this@DeviceBrowserActivity, Observer { status -> handleStatus(status) })
            output.observe(this@DeviceBrowserActivity, Observer { output -> browser.parseResult(output) })
            fileOutput.observe(this@DeviceBrowserActivity, Observer { fileOutput -> handleFileContents(fileOutput) })
            progress.observe(this@DeviceBrowserActivity, Observer { handleProgress(it.first, it.second) })
            error.observe(this@DeviceBrowserActivity, Observer { message ->
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            })
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
        openedItem = item
        AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage(openedItem?.name)
            .setNeutralButton(R.string.file_delete, { v, i -> deleteFile(item) })
            .setPositiveButton(R.string.file_open, { v, i ->
                downloadAction = false
                getFile(item)
            })
            .setNegativeButton(R.string.file_download, { v, i ->
                downloadAction = true
                getFile(item)
            })
            .show()
    }

    private fun handleProgress(progress: Int, max: Int) {
        progress_circular.max = max
        progress_circular.progress = progress
    }

    private fun handleFileContents(fileContents: String) {
        hideProgress()
        when (downloadAction) {
            true -> saveFile(fileContents)
            else -> openFilePreview(fileContents)
        }
    }

    private fun saveFile(fileContents: String) {
        openedItem?.let { openedItem ->
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(dir, openedItem.name)
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos).use { osw ->
                    osw.write(fileContents)
                }
            }
            Toast.makeText(applicationContext, getString(R.string.file_saved_to, file.absolutePath), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun openFilePreview(fileContents: String) {
        val dialog = FileContentsDialog().apply { contents = fileContents }
        val ft = supportFragmentManager.beginTransaction()
        dialog.show(ft, FileContentsDialog.TAG)
    }

    private fun getFile(item: BrowserItem) {
        val path = "${browser.getPathJoined()}${item.name}"
        deviceCommandViewModel.readFile(path)
    }

    private fun deleteFile(item: BrowserItem) {
        // TODO
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