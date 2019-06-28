package com.rpifilebrowser.ui.deviceselect

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.R
import com.rpifilebrowser.bluetooth.BluetoothScanner
import com.rpifilebrowser.model.RemoteDevice
import com.rpifilebrowser.ui.devicebrowser.DeviceBrowserActivity
import com.rpifilebrowser.utils.PermissionsHelper
import com.rpifilebrowser.utils.show
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class DeviceSelectActivity : AppCompatActivity() {

    @Inject
    lateinit var permissionsHelper: PermissionsHelper

    @Inject
    lateinit var bluetoothScanner: BluetoothScanner

    val deviceListAdapter by lazy { DeviceListAdapter(applicationContext) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as FileBrowserApplication).appComponent.inject(this)

        checkBLESupport()
        initializeList()
    }

    override fun onResume() {
        super.onResume()
        handlePermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothScanner.stopScan()
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
                openDevice(device.address)
            }
        }
        devices_list.adapter = deviceListAdapter
        devices_list.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun openDevice(address: String?) {
        bluetoothScanner.stopScan()
        address?.let {
            startActivity(Intent(this, DeviceBrowserActivity::class.java).apply {
                putExtra(DeviceBrowserActivity.DEVICE_ADDRESS_KEY, address)
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

        bluetoothScanner.startDevicesScan()
        bluetoothScanner.onDeviceFound = object : (List<RemoteDevice>) -> Unit {
            override fun invoke(remoteDevices: List<RemoteDevice>) {
                top_label.text = getString(R.string.found_devices, remoteDevices.size)
                deviceListAdapter.swapData(remoteDevices.sortedByDescending { it.rssi })
            }
        }
    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)
}