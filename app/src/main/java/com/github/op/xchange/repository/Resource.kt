package com.github.op.xchange.repository

sealed class Resource<T>(val data: T?) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(data: T?, val throwable: Throwable) : Resource<T>(data)
    class Loading<T>(data: T?) : Resource<T>(data)
    
    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource.Success(data)
        }

        fun <T> error(data: T?, throwable: Throwable): Resource<T> {
            return Resource.Error(data, throwable)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource.Loading(data)
        }
    }
}