package com.github.op.xchange.ui

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.github.op.xchange.XChangeApp
import com.github.op.xchange.injection.XComponent

class ViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val t = super.create(modelClass)
        if (t is XComponent.Injectable && application is XChangeApp) {
            t.inject(application.component)
        }
        return t
    }
}