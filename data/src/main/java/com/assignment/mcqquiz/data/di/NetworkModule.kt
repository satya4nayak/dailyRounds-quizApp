package com.assignment.mcqquiz.data.di

import com.assignment.mcqquiz.data.BuildConfig
import com.assignment.mcqquiz.data.infra.api.QuizRetrofitService
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger module that wires up the network stack.
 *
 * The base URL is sourced from [BuildConfig.BASE_URL], which is set in
 * the :data module's build.gradle.kts so it can be changed per build variant
 * without touching source code.
 */
@Module
class NetworkModule {

    @Provides
    @Singleton
    @Named("baseUrl")
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // time to establish connection
            .readTimeout(60, TimeUnit.SECONDS)    // time to read response body
            .writeTimeout(60, TimeUnit.SECONDS)   // time to send request body
            .build()

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("baseUrl") baseUrl: String,
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(
            json.asConverterFactory("application/json; charset=UTF-8".toMediaType())
        )
        .build()

    @Provides
    @Singleton
    fun provideQuizRetrofitService(retrofit: Retrofit): QuizRetrofitService =
        retrofit.create(QuizRetrofitService::class.java)
}

