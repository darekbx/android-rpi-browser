package com.rpifilebrowser.ui.deviceselect

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.R
import com.rpifilebrowser.bluetooth.BluetoothScanner
import com.rpifilebrowser.model.RemoteDevice
import com.rpifilebrowser.utils.PermissionsHelper
import com.rpifilebrowser.utils.show
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class DeviceSelectActivity : AppCompatActivity() {

    @Inject
    lateinit var permissionsHelper: PermissionsHelper

    @Inject
    lateinit var bluetoothScanner: BluetoothScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as FileBrowserApplication).appComponent.inject(this)

        checkBLESupport()
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

            }
        }
    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)
}