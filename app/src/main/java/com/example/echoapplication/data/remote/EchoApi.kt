package com.example.echoapplication.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface EchoApi {
    @POST("echo")
    suspend fun validate(
        @Body request: EchoRequest
    ): EchoResponse
}

data class EchoRequest(
    val text: String
)

data class EchoResponse(
    val echoedText: String
)