package com.rpifilebrowser.ui.deviceselect

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.R
import com.rpifilebrowser.model.RemoteDevice
import com.rpifilebrowser.ui.devicebrowser.DeviceBrowserActivity
import com.rpifilebrowser.ui.devicessh.DeviceSSHActivity
import com.rpifilebrowser.utils.PermissionsHelper
import com.rpifilebrowser.utils.show
import com.rpifilebrowser.viewmodels.DevicesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class DeviceSelectActivity : AppCompatActivity() {

    companion object {
        val BACKUP_DEVICE = "Backup Device"
        val DEVICE_ADDRESS_KEY = "device_address"
    }

    @Inject
    lateinit var permissionsHelper: PermissionsHelper

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    internal lateinit var devicesViewModel: DevicesViewModel

    val deviceListAdapter by lazy { DeviceListAdapter(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as FileBrowserApplication).appComponent.inject(this)

        devicesViewModel = ViewModelProviders.of(this, viewModelFactory)[DevicesViewModel::class.java]
        devicesViewModel.devices.observe(this@DeviceSelectActivity, Observer { remoteDevices ->
            top_label.text = getString(R.string.found_devices, remoteDevices.size)
            deviceListAdapter.swapData(remoteDevices.sortedByDescending { it.rssi })
        })

        checkBLESupport()
        initializeList()
    }

    override fun onResume() {
        super.onResume()
        handlePermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionsHelper.PERMISSIONS_REQUEST_CODE) {
            val anyDenied = grantResults.any { it == PackageManager.PERMISSION_DENIED }
            when (anyDenied) {
                true -> Toast.makeText(applicationContext, R.string.permissions_are_required, Toast.LENGTH_SHORT).show()
                else -> startScan()
            }
        }
    }

    private fun initializeList() {
        deviceListAdapter.onDeviceClick = object : (RemoteDevice) -> Unit {
            override fun invoke(device: RemoteDevice) {
                devicesViewModel.stopScan()
                showActionDialog(device)
                //openDevice(device.address)
            }
        }
        devices_list.adapter = deviceListAdapter
        devices_list.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun showActionDialog(remoteDevice: RemoteDevice) {
        AlertDialog.Builder(this)
            .setTitle(remoteDevice.name)
            .setMessage(R.string.select_action)
            .setPositiveButton(R.string.terminal, { v, i-> openSSH(remoteDevice.address) })
            .setNegativeButton(R.string.browser, {v,i-> openBrowser(remoteDevice.address) })
            .show()
    }

    private fun openSSH(address: String?) {
        startDeviceActivity(address, DeviceSSHActivity::class.java)
    }

    private fun openBrowser(address: String?) {
        startDeviceActivity(address, DeviceBrowserActivity::class.java)
    }

    private fun startDeviceActivity(address: String?, activity: Class<out Any>) {
        address?.let {
            startActivity(Intent(this, activity).apply {
                putExtra(DEVICE_ADDRESS_KEY, address)
            })
        }
    }

    private fun checkBLESupport() {
        packageManager
            .takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }
            ?.also {
                Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun handlePermissions() {
        val hasPermissions = permissionsHelper.checkAllPermissionsGranted(this)
        when (hasPermissions) {
            true -> startScan()
            else -> permissionsHelper.requestPermissions(this)
        }
    }

    private fun startScan() {
        top_label.text = getString(R.string.searching_for_devices)
        discover_progress.show()
        devicesViewModel.startScan()
    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)
}