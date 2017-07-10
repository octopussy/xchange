package com.github.op.xchange.repository

sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val data: T?, val throwable: Throwable) : Resource<T>()
    class Loading<T>(val data: T?) : Resource<T>()
    
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