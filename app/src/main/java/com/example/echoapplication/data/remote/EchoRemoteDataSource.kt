package com.example.echoapplication.data.remote

import kotlinx.coroutines.delay
import javax.inject.Inject

private const val FAKE_SERVER_DELAY = 1000L
class EchoRemoteDataSource @Inject constructor(
    private val api: EchoApi
) {
    suspend fun submit(text: String): EchoResponse {
        delay(FAKE_SERVER_DELAY)
        return api.validate(EchoRequest(text))
    }
}