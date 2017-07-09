package com.github.op.xchange.api

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface FixerApi {
    @GET("/latest")
    fun defaultCall(): Flowable<FixerResponse>

    @GET("/latest")
    fun updateRateHistory(@Query("base") baseCurrency: String, @Query("symbols") relatedCurrency: String): Flowable<FixerResponse>
}