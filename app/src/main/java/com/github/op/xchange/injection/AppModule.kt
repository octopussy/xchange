package com.github.op.xchange.injection

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.db.XChangeDatabase
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

    @Provides @Singleton
    fun db(context: Context): XChangeDatabase
            = Room.databaseBuilder(context, XChangeDatabase::class.java, "xchange_db").build()

}