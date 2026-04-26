package com.example.echoapplication.domain

interface EchoRepository {
    suspend fun submit(text: String): EchoResult
}