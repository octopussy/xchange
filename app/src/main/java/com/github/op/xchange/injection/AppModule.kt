package com.github.op.xchange.injection

import android.content.Context
import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.BuildConfig
import com.github.op.xchange.api.Api
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule(private val applicationContext: Context) {

    @Provides @Singleton
    fun applicationContext() = applicationContext

    @Provides @Singleton
    fun gson(): Gson = GsonBuilder().create()

    @Provides @Singleton
    fun prefs(context: Context): SharedPreferences = context.getSharedPreferences("XChangeSharedPrefs", Context.MODE_PRIVATE)

    @Provides @Singleton
    fun rxPrefs(prefs: SharedPreferences): RxSharedPreferences = RxSharedPreferences.create(prefs)

    @Provides @Singleton
    fun yqlRestApi(gson: Gson): Api {
        val builder = OkHttpClient.Builder()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        builder.addInterceptor(interceptor)

        val ENDPOINT = "https://query.yahooapis.com/v1/public/yql"

        val retrofit = Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return retrofit.create(Api::class.java)
    }
}