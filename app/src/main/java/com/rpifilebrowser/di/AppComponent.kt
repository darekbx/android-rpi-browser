package com.rpifilebrowser.di

import com.rpifilebrowser.ui.deviceselect.DeviceSelectActivity
import com.rpifilebrowser.di.viewmodels.ViewModelModule
import com.rpifilebrowser.ui.devicessh.DeviceBrowserActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, ViewModelModule::class))
interface AppComponent {

    fun inject(deviceSelectActivity: DeviceSelectActivity)
    fun inject(deviceBrowserActivity: DeviceBrowserActivity)
}