package com.example.echoapplication.di

import com.example.echoapplication.data.repository.EchoRepositoryImpl
import com.example.echoapplication.domain.EchoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEchoRepository(
        implementation: EchoRepositoryImpl
    ): EchoRepository
}