package com.rpifilebrowser.di

import com.rpifilebrowser.MainActivity
import com.rpifilebrowser.di.viewmodels.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, ViewModelModule::class))
interface AppComponent {

    fun inject(mainActivity: MainActivity)

}