package com.jainhardik120.jiitcompanion.di

import com.jainhardik120.jiitcompanion.data.repository.PortalRepositoryImpl
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPortalRepository(
        portalRepositoryImpl: PortalRepositoryImpl
    ) : PortalRepository
}