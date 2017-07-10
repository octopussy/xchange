package com.github.op.xchange.repository

import android.arch.lifecycle.LiveData
import android.support.annotation.MainThread
import android.support.annotation.NonNull
import android.support.annotation.WorkerThread
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class NetworkResourceProvider<ResultType, RequestType> : ResourceProvider<ResultType>() {

    override fun reload() {
        if (_result.value == null) {
            _result.setValue(Resource.loading(null))
        }
        val dbSource = loadFromDb()
        _result.addSource(dbSource) { data ->
            _result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                _result.addSource(dbSource) {
                    newData ->
                    _result.setValue(Resource.success(newData!!))
                }
            }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createApiCall()

        _result.addSource(dbSource) { newData ->
            _result.setValue(Resource.loading(newData))
        }

        apiResponse.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, error ->
                    _result.removeSource(dbSource)

                    if (error == null) {
                        Completable.fromCallable { saveCallResult(result) }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    _result.addSource(loadFromDb()) {
                                        newData ->
                                        _result.value = Resource.success(newData!!)
                                    }
                                }, {

                                })
                    } else {
                        onFetchFailed()
                        _result.addSource(dbSource) { newData ->
                            _result.value = Resource.error(newData, error)
                        }
                    }
                }
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @NonNull
    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @NonNull
    @MainThread
    protected abstract fun createApiCall(): Single<RequestType>

    @MainThread
    protected fun onFetchFailed() {
    }

}