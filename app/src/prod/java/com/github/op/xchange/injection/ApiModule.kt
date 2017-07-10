package com.github.op.xchange.injection

import com.github.op.xchange.BuildConfig
import com.github.op.xchange.api.RemoteApi
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
    fun remoteApi(gson: Gson): RemoteApi {
        val builder = OkHttpClient.Builder()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        builder.addInterceptor(interceptor)

        val ENDPOINT = "http://api.fixer.io/"

        val retrofit = Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return retrofit.create(RemoteApi::class.java)
    }

}