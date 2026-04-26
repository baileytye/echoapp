package com.example.echoapplication.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONObject
import javax.inject.Inject

class FakeServerInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.method == "POST" && request.url.encodedPath == "/echo") {
            val buffer = Buffer()
            request.body?.writeTo(buffer)

            val body = buffer.readUtf8()
            val text = JSONObject(body).optString("text")

            val isValid = text.isNotBlank() &&
                    !text.contains("fail", ignoreCase = true) &&
                    !text.contains("error", ignoreCase = true)

            val responseCode = if (isValid) 200 else 400

            val responseJson = if (isValid) {
                """{"echoedText":${JSONObject.quote(text)}}"""
            } else {
                """{"message":"Server validation failed. Please enter valid text."}"""
            }

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(responseCode)
                .message(if (isValid) "OK" else "Bad Request")
                .body(responseJson.toResponseBody("application/json".toMediaType()))
                .build()
        }

        return chain.proceed(request)
    }
}