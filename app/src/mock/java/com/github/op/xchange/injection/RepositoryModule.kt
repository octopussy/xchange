package com.github.op.xchange.injection

import com.github.op.xchange.repository.FakeXChangeRepository
import com.github.op.xchange.repository.XChangeRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides @Singleton
    fun repository(): XChangeRepository = FakeXChangeRepository()
}