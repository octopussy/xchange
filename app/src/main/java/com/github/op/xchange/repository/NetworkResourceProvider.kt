package com.github.op.xchange.repository

import com.github.op.xchange.api.ApiResponse
import android.arch.lifecycle.LiveData
import android.support.annotation.MainThread
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.support.annotation.WorkerThread
import android.os.AsyncTask


/*
abstract class NetworkResourceProvider<T> : ResourceProvider<T>() {

    init {
        _result.setValue(Resource.loading(null))
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

    private fun fetchFromNetwork(dbSource: LiveData<T>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source,
        // it will dispatch its latest value quickly
        _result.addSource(dbSource) {
            newData ->
            _result.setValue(Resource.loading(newData))
        }
        _result.addSource(apiResponse) { response ->
            _result.removeSource(apiResponse)
            _result.removeSource(dbSource)

            if (response.isSuccessful()) {
                saveResultAndReInit(response)
            } else {
                onFetchFailed()
                _result.addSource(dbSource
                ) { newData ->
                    _result.setValue(Resource.error(response.errorMessage, newData))
                }
            }
        }
    }

    @MainThread
    private fun saveResultAndReInit(response: ApiResponse<RequestType>) {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                saveCall_result(response.body)
                return null
            }

            override fun onPostExecute(aVoid: Void) {
                // we specially request a new live data,
                // otherwise we will get immediately last cached value,
                // which may not be updated with latest _results received from network.
                _result.addSource(loadFromDb()) {
                    newData ->
                    _result.setValue(Resource.success(newData))
                }
            }
        }.execute()
    }

    @WorkerThread
    protected abstract fun saveCall_result(@NonNull item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(@Nullable data: T): Boolean

    @NonNull
    @MainThread
    protected abstract fun loadFromDb(): LiveData<T>

    @NonNull
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    @MainThread
    protected fun onFetchFailed() {
    }

}*/
