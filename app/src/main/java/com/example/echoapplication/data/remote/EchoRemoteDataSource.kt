package com.example.echoapplication.data.remote

import javax.inject.Inject

class EchoRemoteDataSource @Inject constructor(
    private val api: EchoApi
) {
    suspend fun submit(text: String): EchoResponse {
        return api.validate(EchoRequest(text))
    }
}