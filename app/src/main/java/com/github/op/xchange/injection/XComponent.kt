package com.github.op.xchange.injection

import com.github.op.xchange.ui.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, RepositoryModule::class))
interface XComponent {

    fun inject(mainViewModel: MainViewModel)

    interface Injectable {
        fun inject(component: XComponent)
    }
}