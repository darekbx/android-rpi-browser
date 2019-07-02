package com.rpifilebrowser.di.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rpifilebrowser.viewmodels.DeviceCommandViewModel
import com.rpifilebrowser.viewmodels.DevicesViewModel
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
    @ViewModelFactory.ViewModelKey(DeviceCommandViewModel::class)
    internal abstract fun bindDeviceCommandViewModel(viewModel: DeviceCommandViewModel): ViewModel


}