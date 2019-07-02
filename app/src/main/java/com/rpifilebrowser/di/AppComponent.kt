package com.rpifilebrowser.di

import com.rpifilebrowser.ui.deviceselect.DeviceSelectActivity
import com.rpifilebrowser.di.viewmodels.ViewModelModule
import com.rpifilebrowser.ui.devicebrowser.DeviceBrowserActivity
import com.rpifilebrowser.ui.devicessh.DeviceSSHActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, ViewModelModule::class))
interface AppComponent {

    fun inject(deviceSelectActivity: DeviceSelectActivity)
    fun inject(deviceSSHActivity: DeviceSSHActivity)
    fun inject(deviceBrowserActivity: DeviceBrowserActivity)
}