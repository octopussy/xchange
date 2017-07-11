package com.github.op.xchange.util

import android.view.View

open class BaseActivity : android.support.v7.app.AppCompatActivity(), android.arch.lifecycle.LifecycleRegistryOwner {
    private val registry = android.arch.lifecycle.LifecycleRegistry(this)
    override fun getLifecycle(): android.arch.lifecycle.LifecycleRegistry = registry
}

var android.view.View.visible: Boolean
    get() = visibility == android.view.View.VISIBLE
    set(value) {
        visibility = if (value) android.view.View.VISIBLE else android.view.View.GONE
    }