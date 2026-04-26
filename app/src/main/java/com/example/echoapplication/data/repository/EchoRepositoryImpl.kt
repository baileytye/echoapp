package com.example.echoapplication.data.repository

import com.example.echoapplication.data.remote.EchoRemoteDataSource
import com.example.echoapplication.domain.EchoRepository
import com.example.echoapplication.domain.EchoResult
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class EchoRepositoryImpl @Inject constructor(
    private val remoteDataSource: EchoRemoteDataSource
) : EchoRepository {

    override suspend fun submit(text: String): EchoResult {
        return try {
            val response = remoteDataSource.submit(text)
            EchoResult.Success(response.echoedText)
        } catch (exception: HttpException) {
            EchoResult.Error("Server validation failed. Please enter valid text.")
        } catch (exception: IOException) {
            EchoResult.Error("Network error. Please try again.")
        } catch (exception: Exception) {
            EchoResult.Error("Something went wrong. Please try again.")
        }
    }
}