package com.github.op.xchange.injection

import com.github.op.xchange.api.QuoteDTO
import com.github.op.xchange.api.QuotesApi
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import org.threeten.bp.LocalDateTime
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides @Singleton
    fun remoteApi(): QuotesApi {

        return object : QuotesApi {
            override fun getLatestQuote(pair: String): Single<List<QuoteDTO>> {
                return Single.just(listOf(QuoteDTO(pair, LocalDateTime.now(), (Math.random() * 10f + 0.1f).toFloat())))
            }
        }
    }
}