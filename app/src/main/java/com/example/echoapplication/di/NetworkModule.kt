package com.example.echoapplication.di

import com.example.echoapplication.data.remote.EchoApi
import com.example.echoapplication.data.remote.FakeServerInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val FAKE_SERVER = "https://fake-echo-server.com/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        fakeServerInterceptor: FakeServerInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(fakeServerInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FAKE_SERVER)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    @Provides
    @Singleton
    fun provideEchoApi(retrofit: Retrofit): EchoApi {
        return retrofit.create(EchoApi::class.java)
    }
}