package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData

abstract class ResourceProvider<T> {
    protected val _result = MediatorLiveData<Resource<T>>()
    val result: LiveData<Resource<T>>
        get() = _result

    abstract fun reload()
}