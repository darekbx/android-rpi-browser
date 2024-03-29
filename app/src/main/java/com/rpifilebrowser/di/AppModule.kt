package com.rpifilebrowser.di

import android.content.Context
import com.rpifilebrowser.FileBrowserApplication
import com.rpifilebrowser.bluetooth.BluetoothCommand
import com.rpifilebrowser.bluetooth.BluetoothScanner
import com.rpifilebrowser.ui.devicebrowser.tools.Browser
import com.rpifilebrowser.ui.devicebrowser.tools.OutputParser
import com.rpifilebrowser.utils.PermissionsHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val application: FileBrowserApplication) {

    @Provides
    @Singleton
    fun provideApplication(): FileBrowserApplication = application

    @Provides
    fun provideContext(): Context = application

    @Provides
    fun providePermissionsHelper() = PermissionsHelper()

    @Provides
    fun provideBluetoothScanner(context: Context) = BluetoothScanner(context)

    @Provides
    fun provideBluetoothCommand(context: Context) = BluetoothCommand(context)

    @Provides
    fun provideOutputParser() = OutputParser()

    @Provides
    fun provideBrowser(outputParser: OutputParser) = Browser(outputParser)
}