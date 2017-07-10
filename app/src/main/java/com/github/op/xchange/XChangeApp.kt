package com.github.op.xchange

import android.app.Application
import android.content.Intent
import com.github.op.xchange.injection.AppModule
import com.github.op.xchange.injection.DaggerXComponent
import com.github.op.xchange.injection.XComponent
import com.jakewharton.threetenabp.AndroidThreeTen

class XChangeApp : Application() {

    private lateinit var _component: XComponent
    val component get() = _component

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        _component = DaggerXComponent.builder().appModule(AppModule(this)).build()

        startService(Intent(this, UpdateService::class.java))
    }
}
