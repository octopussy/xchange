package com.github.op.xchange

import android.app.Application
import com.github.op.xchange.injection.DaggerXComponent
import com.github.op.xchange.injection.XComponent
import com.github.op.xchange.injection.AppModule

class XChangeApp : Application() {

    private lateinit var _component: XComponent
    val component get() = _component

    override fun onCreate() {
        super.onCreate()
        _component = DaggerXComponent.builder().appModule(AppModule(this)).build()
    }
}
