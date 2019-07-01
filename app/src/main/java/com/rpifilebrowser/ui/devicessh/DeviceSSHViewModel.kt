package com.rpifilebrowser.ui.devicessh

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rpifilebrowser.bluetooth.BluetoothCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceSSHViewModel @Inject constructor(private val bluetoothCommand: BluetoothCommand): ViewModel() {

    init {
        with(bluetoothCommand) {
            onStatus = {
                CoroutineScope(Dispatchers.Main).launch {
                    status.value = it
                }
            }
            onCommandResult = {
                CoroutineScope(Dispatchers.Main).launch {
                    output.value = it
                }
            }
        }
    }

    val output: MutableLiveData<String> = MutableLiveData()
    val status: MutableLiveData<Int> = MutableLiveData()

    fun connect(deviceAddress: String) {
        bluetoothCommand.connect(deviceAddress)
    }

    fun executeCommand(command: String) {
        bluetoothCommand.executeCommand(command)
    }

    fun dispose() {
        bluetoothCommand.dispose()
    }

    override fun onCleared() {
        super.onCleared()
        dispose()
    }
}