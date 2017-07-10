package com.github.op.xchange.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteApi {

    @GET("/latest")
    fun getLatestRate(@Query("base") baseCurrency: String, @Query("symbols") relatedCurrency: String): Single<ApiResponse>

    @GET("/latest")
    fun getLatestRates(@Query("base") baseCurrency: String): Single<ApiResponse>
}