package com.rpifilebrowser.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rpifilebrowser.bluetooth.BluetoothScanner
import com.rpifilebrowser.model.RemoteDevice
import javax.inject.Inject

class DevicesViewModel  @Inject constructor(private val bluetoothScanner: BluetoothScanner): ViewModel() {

    val devices: MutableLiveData<MutableList<RemoteDevice>> = MutableLiveData()

    init {
        bluetoothScanner.onDeviceFound = { remoteDevices -> updateDevices(remoteDevices) }
    }

    fun startScan() {
        bluetoothScanner.startDevicesScan()
    }

    private fun updateDevices(remoteDevices: List<RemoteDevice>) {
        if (devices.value == null) {
            devices.value = mutableListOf()
        }
        devices.value?.run {
            clear()
            addAll(remoteDevices.sortedByDescending { it.rssi })
        }
        this.devices.value = devices.value
    }

    fun stopScan() {
        bluetoothScanner.stopScan()
    }

    override fun onCleared() {
        super.onCleared()
        stopScan()
    }
}