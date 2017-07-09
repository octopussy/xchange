package com.github.op.xchange.injection

import com.github.op.xchange.ui.main.MainViewModel
import com.github.op.xchange.ui.settings.SettingsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, RepositoryModule::class))
interface XComponent {

    fun inject(activity: SettingsActivity)

    fun inject(mainViewModel: MainViewModel)

    interface Injectable {
        fun inject(component: XComponent)
    }
}