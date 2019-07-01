package com.rpifilebrowser.di.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rpifilebrowser.ui.devicessh.DeviceSSHViewModel
import com.rpifilebrowser.ui.deviceselect.DevicesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelFactory.ViewModelKey(DevicesViewModel::class)
    internal abstract fun bindDevicesViewModel(viewModel: DevicesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelFactory.ViewModelKey(DeviceSSHViewModel::class)
    internal abstract fun bindDeviceSSHViewModel(viewModel: DeviceSSHViewModel): ViewModel

}