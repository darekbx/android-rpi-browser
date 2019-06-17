package com.rpifilebrowser.di

import android.content.Context
import com.rpifilebrowser.FileBrowserApplication
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
}