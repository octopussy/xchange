package com.github.op.xchange.api

import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FixerApi {
    @GET("/latest")
    fun defaultCall(): Flowable<FixerResponse>

    @GET("/latest")
    fun defaultCall2(): Single<FixerResponse>

    @GET("/latest")
    fun fetchLatestRate(@Query("base") baseCurrency: String, @Query("symbols") relatedCurrency: String): Flowable<FixerResponse>
}