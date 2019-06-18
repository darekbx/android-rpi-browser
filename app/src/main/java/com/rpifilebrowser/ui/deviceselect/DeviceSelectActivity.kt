package com.rpifilebrowser.ui.deviceselect

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.R
import com.rpifilebrowser.utils.PermissionsHelper
import javax.inject.Inject

class DeviceSelectActivity : AppCompatActivity() {

    @Inject
    lateinit var permissionsHelper: PermissionsHelper

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionsHelper.PERMISSIONS_REQUEST_CODE) {
            val anyDenied = grantResults.any { it == PackageManager.PERMISSION_DENIED }
            if (anyDenied) {
                Toast.makeText(applicationContext, R.string.permissions_are_required, Toast.LENGTH_SHORT).show()
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
        if (!hasPermissions) {
            permissionsHelper.requestPermissions(this)
        }
    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)
}