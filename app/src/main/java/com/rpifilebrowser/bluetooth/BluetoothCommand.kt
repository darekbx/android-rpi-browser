package com.rpifilebrowser.bluetooth

import android.bluetooth.*
import android.content.Context
import com.rpifilebrowser.bluetooth.utils.Chunker
import com.rpifilebrowser.bluetooth.utils.Merger
import java.util.*

class BluetoothCommand(context: Context) : BaseBluetooth(context) {

    val BT_SERVICE = "eabea763-8144-4652-a831-82fc9d4e645c"
    val BT_WRITE_CHARACTERISTIC = "2e7a6c4b-b70e-49b6-acf9-2be297ac29e9"
    val BT_NOTIFY_CHARACTERISTIC = "25db62b2-00d3-4df9-b7e0-125623d67008"

    private var gatt: BluetoothGatt? = null
    private var commandChunks = listOf<String>()
    private var chunkIndex = 0

    var onCommandResult: ((output: String) -> Unit)? = null
    var onProgress: ((progress: Int, max: Int) -> Unit)? = null
        set(value) {
            Merger.onProgress = value
            field = value
        }

    fun executeCommand(command: String) {
        gatt?.let { gatt ->
            commandChunks = Chunker.commandToChunks(command)
            chunkIndex = 0
            val service = gatt.getService(UUID.fromString(BT_SERVICE))
            service?.getCharacteristic(UUID.fromString(BT_WRITE_CHARACTERISTIC))
                ?.apply {
                    setValue(commandChunks[chunkIndex])
                    chunkIndex++
                    onProgress?.invoke(chunkIndex, commandChunks.size)
                }
                ?.run {
                    gatt.writeCharacteristic(this)
                    log("Chunk written")
                }
        }
    }

    fun connect(deviceAddress: String) {
        var device = bluetoothAdapter?.getRemoteDevice(deviceAddress.toUpperCase())
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
        value?.let { value ->
            Merger.obtainPacket(value, { mergedOutput ->
                onCommandResult?.invoke(mergedOutput)
            })
        }
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

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            writeNextChunk(characteristic, gatt)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            handleIncomingValue(characteristic)
        }

        private fun writeNextChunk(characteristic: BluetoothGattCharacteristic?, gatt: BluetoothGatt?) {
            if (chunkIndex <= commandChunks.size - 1) {
                characteristic?.setValue(commandChunks[chunkIndex])
                gatt?.writeCharacteristic(characteristic)
                chunkIndex++
                onProgress?.invoke(chunkIndex, commandChunks.size)
            }
        }
    }
}