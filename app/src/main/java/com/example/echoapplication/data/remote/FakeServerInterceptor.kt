package com.example.echoapplication.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class FakeServerInterceptor @Inject constructor() : Interceptor {

    private val validRequestCounter = AtomicInteger(0) // Prevents race condition and happens instantaneusly

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.method == "POST" && request.url.encodedPath == "/echo") {
            val buffer = Buffer()
            request.body?.writeTo(buffer)

            val body = buffer.readUtf8()
            val text = JSONObject(body).optString("text").trim()

            val isBlank = text.isBlank()

            val shouldSucceed = if (isBlank) {
                false
            } else {
                val requestNumber = validRequestCounter.incrementAndGet()
                requestNumber % 2 == 1
            }

            val responseCode = if (shouldSucceed) 200 else 400

            val responseJson = if (shouldSucceed) {
                """{"echoedText":${JSONObject.quote(text)}}"""
            } else {
                """{"message":"Fake server rejected this request."}"""
            }

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(responseCode)
                .message(if (shouldSucceed) "OK" else "Bad Request")
                .body(responseJson.toResponseBody("application/json".toMediaType()))
                .build()
        }

        return chain.proceed(request)
    }
}