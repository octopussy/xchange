package com.github.op.xchange.injection

import com.github.op.xchange.BuildConfig
import com.github.op.xchange.api.QuotesApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule {
    @Provides @Singleton
    fun remoteApi(gson: Gson): QuotesApi {

        val ENDPOINT = "https://forex.1forge.com/"

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        val builder = OkHttpClient.Builder()
                .addInterceptor(interceptor)

        val retrofit = Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()


        // 1forge key t8b06ZdhJHrSZtZEIIrd8VVcTd8nbLgJ

        return retrofit.create(QuotesApi::class.java)
    }
}