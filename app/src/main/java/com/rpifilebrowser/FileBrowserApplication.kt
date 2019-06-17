package com.rpifilebrowser

import android.app.Application
import com.rpifilebrowser.di.AppComponent
import com.rpifilebrowser.di.AppModule
import com.rpifilebrowser.di.DaggerAppComponent

class FileBrowserApplication  : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}