package com.github.op.xchange.injection

import android.content.Context
import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
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
}