package com.rpifilebrowser.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rpifilebrowser.bluetooth.BluetoothCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceCommandViewModel @Inject constructor(private val bluetoothCommand: BluetoothCommand): ViewModel() {

    init {
        with(bluetoothCommand) {
            onStatus = {
                CoroutineScope(Dispatchers.Main).launch {
                    status.value = it
                }
            }
            onCommandResult = {
                CoroutineScope(Dispatchers.Main).launch {
                    when (isFileReadRequest) {
                        true -> fileOutput.value = it
                        else -> output.value = it
                    }
                }
            }
        }
    }

    private var isFileReadRequest = false

    val output: MutableLiveData<String> = MutableLiveData()
    val status: MutableLiveData<Int> = MutableLiveData()
    val fileOutput: MutableLiveData<String> = MutableLiveData()

    fun connect(deviceAddress: String) {
        bluetoothCommand.connect(deviceAddress)
    }

    fun readFile(path: String) {
        isFileReadRequest = true
        bluetoothCommand.executeCommand("cat $path")
    }

    fun executeCommand(command: String) {
        isFileReadRequest = false
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