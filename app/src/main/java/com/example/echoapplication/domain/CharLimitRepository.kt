package com.example.echoapplication.domain

interface CharLimitRepository {
    suspend fun getCharLimit(): CharLimitConfig
}
