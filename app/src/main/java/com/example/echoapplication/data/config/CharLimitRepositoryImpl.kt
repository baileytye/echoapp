package com.example.echoapplication.data.config

import com.example.echoapplication.domain.CharLimitConfig
import com.example.echoapplication.domain.CharLimitRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharLimitRepositoryImpl @Inject constructor() : CharLimitRepository {
    override suspend fun getCharLimit(): CharLimitConfig {
        delay(300L)
        return CharLimitConfig(maxLength = 150)
    }
}
