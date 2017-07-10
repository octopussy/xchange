package com.github.op.xchange.injection

import com.github.op.xchange.api.ApiResponse
import com.github.op.xchange.api.RemoteApi
import com.github.op.xchange.entity.Currency
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides @Singleton
    fun remoteApi(): RemoteApi {

        return object : RemoteApi {
            override fun getLatestRate(baseCurrency: String, relatedCurrency: String): Single<ApiResponse> {
                return Single.just(ApiResponse(baseCurrency, genRandomRates(baseCurrency)))
            }
        }
    }

    companion object {
        private fun genRandomRates(base: String): Map<String, Float>
                = Currency.values().filter { it.name != base }.associate { it.name to (Math.random() * 100f + 5f).toFloat() }
    }
}