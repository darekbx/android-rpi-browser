package com.rpifilebrowser.bluetooth

import android.bluetooth.*
import android.content.Context
import java.util.*

class BluetoothCommand(context: Context) : BaseBluetooth(context) {

    val BT_SERVICE = "00000000-1111-2222-3333-000000000001"
    val BT_WRITE_CHARACTERISTIC = "00000000-1111-2222-3333-000000000010"
    val BT_NOTIFY_CHARACTERISTIC = "00000000-1111-2222-3333-000000000020"

    private var gatt: BluetoothGatt? = null

    fun writeAction(action: String) {
        gatt?.let { gatt ->
            val service = gatt.getService(UUID.fromString(BT_SERVICE))
            service?.getCharacteristic(UUID.fromString(BT_WRITE_CHARACTERISTIC))
                ?.apply {

                    // TODO: write big data
                    setValue(action)

                }
                ?.run {
                    gatt.writeCharacteristic(this)
                    log("Chunk written")
                }
        }
    }

    fun connect(deviceAddress: String) {

        var device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        if (device != null) {
            gatt = device.connectGatt(context, true, gattCallback)
        }
    }

    fun dispose() {
        gatt?.disconnect()
        gatt?.close()
    }

    private fun handleGattConnection(newState: Int, gatt: BluetoothGatt?) {
        when (newState) {
            BluetoothProfile.STATE_CONNECTING -> log("GATT is connecting...")
            BluetoothProfile.STATE_CONNECTED -> discoverServices(gatt)
            BluetoothProfile.STATE_DISCONNECTED -> handleGattDisconnected(gatt)
        }
    }

    private fun discoverServices(gatt: BluetoothGatt?) {
        log("GATT is connected")
        gatt?.discoverServices()
    }

    private fun handleGattDisconnected(gatt: BluetoothGatt?) {
        log("GATT is disconnected, retrying...")
        onStatus?.invoke(ERROR_DISCONNECTED)
        dispose()
        gatt?.device?.let { device ->
            connect(device.address)
        }
    }

    private fun handleServices(status: Int, gatt: BluetoothGatt?) {
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> handleServicesSuccess(gatt)
            else -> handleServicesError()
        }
    }

    private fun handleServicesSuccess(gatt: BluetoothGatt?) {
        log("GATT discovery success")
        val service = gatt?.getService(UUID.fromString(BT_SERVICE))
        service?.let { enableNotifications(it, gatt) }
    }

    private fun enableNotifications(service: BluetoothGattService, gatt: BluetoothGatt) {
        val characteristicNotify = service.getCharacteristic(UUID.fromString(BT_NOTIFY_CHARACTERISTIC))
        gatt.setCharacteristicNotification(characteristicNotify, true)
        characteristicNotify.descriptors.forEach {
            it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(it)
        }
        log("GATT ready!")
        onStatus?.invoke(STATUS_SUCCESS)
    }

    private fun handleServicesError() {
        log("GATT discovery failed")
        onStatus?.invoke(ERROR_GATT_FAILED)
    }

    private fun handleIncomingValue(characteristic: BluetoothGattCharacteristic?) {
        val value = characteristic?.getStringValue(0)

        // TODO: read big data

        log("GATT characteristic value: $value")
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            handleGattConnection(newState, gatt)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            handleServices(status, gatt)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            handleIncomingValue(characteristic)
        }
    }
}