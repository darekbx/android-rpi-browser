package com.rpifilebrowser.bluetooth

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import com.rpifilebrowser.model.RemoteDevice

class BluetoothScanner(context: Context): BaseBluetooth(context) {

    private var devices = mutableListOf<RemoteDevice>()

    var onDeviceFound: ((remoteDevices: List<RemoteDevice>) -> Unit)? = null

    fun startDevicesScan() {
        bluetoothAdapter?.bluetoothLeScanner?.startScan(leScanCallback)
    }

    fun stopScan() {
        bluetoothLeScanner?.stopScan(leScanCallback)
        bluetoothAdapter?.cancelDiscovery()
    }

    private val leScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                val remoteDevice = RemoteDevice(
                    it.device.name,
                    it.device.address,
                    it.rssi,
                    it.isConnectable)

                val hasDevice = devices.any { it.address == remoteDevice.address }
                if (!hasDevice) {
                    devices.add(remoteDevice)

                    onDeviceFound?.invoke(devices)
                    log("Found device ${it.device.name}")
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            onStatus?.invoke(ERROR_SCAN_FAILED)
            log("Start failed $errorCode")
        }
    }
}