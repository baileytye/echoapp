package com.example.echoapplication.di

import com.example.echoapplication.data.config.CharLimitRepositoryImpl
import com.example.echoapplication.domain.CharLimitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class CharLimitModule {

    @Binds
    abstract fun bindCharLimitRepository(
        implementation: CharLimitRepositoryImpl
    ): CharLimitRepository
}
