package com.github.op.xchange.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface QuotesApi {

    @GET("/1.0.2/quotes?api_key=t8b06ZdhJHrSZtZEIIrd8VVcTd8nbLgJ")
    fun getLatestQuote(@Query("pairs") pair: String): Single<List<QuoteDTO>>
}