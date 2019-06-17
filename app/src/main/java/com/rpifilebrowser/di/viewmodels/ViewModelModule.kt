package com.rpifilebrowser.di.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    /*
    @Binds
    @IntoMap
    @ViewModelFactory.ViewModelKey(DotViewModel::class)
    internal abstract fun bindDotViewModel(viewModel: DotViewModel): ViewModel
    */
}