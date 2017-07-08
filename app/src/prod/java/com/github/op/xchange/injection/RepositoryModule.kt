package com.github.op.xchange.injection

import android.arch.persistence.room.Room
import android.content.Context
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.db.XChangeDatabase
import com.github.op.xchange.repository.XChangeRepository
import com.github.op.xchange.repository.XChangeRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides @Singleton
    fun db(context: Context): XChangeDatabase
            = Room.databaseBuilder(context, XChangeDatabase::class.java, "xchange_db").build()

    @Provides @Singleton
    fun repository(db: XChangeDatabase, rxPrefs: RxSharedPreferences): XChangeRepository
            = XChangeRepositoryImpl(db, rxPrefs)
}