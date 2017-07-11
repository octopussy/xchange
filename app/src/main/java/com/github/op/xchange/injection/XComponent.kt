package com.github.op.xchange.injection

import com.github.op.xchange.UpdateService
import com.github.op.xchange.XChangeApp
import com.github.op.xchange.ui.viewModel.MainViewModel
import com.github.op.xchange.ui.settings.SettingsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, RepositoryModule::class, ApiModule::class))
interface XComponent {

    fun inject(xChangeApp: XChangeApp)

    fun inject(updateService: UpdateService)

    fun inject(activity: SettingsActivity)

    fun inject(mainViewModel: MainViewModel)

    interface Injectable {
        fun inject(component: XComponent)
    }
}