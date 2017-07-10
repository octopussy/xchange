package com.github.op.xchange.injection

import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.op.xchange.api.RemoteApi
import com.github.op.xchange.db.XChangeDatabase
import com.github.op.xchange.repository.XChangeRepository
import com.github.op.xchange.repository.XChangeRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides @Singleton
    fun repository(remoteApi: RemoteApi, db: XChangeDatabase, rxPrefs: RxSharedPreferences): XChangeRepository
            = XChangeRepositoryImpl(remoteApi, db, rxPrefs)
}