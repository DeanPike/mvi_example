package com.domain.mvi.util

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilityModule {
    @Binds
    @Singleton
    abstract fun provideDispatchers(impl: DispatcherProviderImpl): DispatcherProvider
}