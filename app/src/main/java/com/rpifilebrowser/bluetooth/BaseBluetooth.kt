package com.rpifilebrowser.bluetooth

import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log

open class BaseBluetooth(val context: Context) {

    companion object {
        val ERROR_SCAN_FAILED = 1
        val ERROR_DISCONNECTED = 2
        val ERROR_GATT_FAILED = 3
        val STATUS_SUCCESS = 10
    }

    val onStatus: ((status: Int) -> Unit)? = null

    protected fun log(message: String) {
        Log.v("BluetoothCommunication", message)
    }

    protected val bluetoothLeScanner by lazy { bluetoothAdapter?.bluetoothLeScanner }
    protected val bluetoothAdapter by lazy { bluetoothManager.adapter?.takeIf { it.isEnabled } }
    protected val bluetoothManager by lazy { context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
}