package com.akshayashokcode.youtubeandroid.di

import com.akshayashokcode.youtubeandroid.core.network.ApiKeyInterceptor
import com.akshayashokcode.youtubeandroid.core.network.OAuthInterceptor
import com.akshayashokcode.youtubeandroid.core.network.YouTubeApiService
import com.akshayashokcode.youtubeandroid.core.network.YouTubeOAuthApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://www.googleapis.com/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    @Provides
    @Singleton
    @ApiKeyClient
    fun provideApiKeyOkHttpClient(apiKeyInterceptor: ApiKeyInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor())
            .build()

    @Provides
    @Singleton
    @OAuthClient
    fun provideOAuthOkHttpClient(oauthInterceptor: OAuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(oauthInterceptor)
            .addInterceptor(loggingInterceptor())
            .build()

    @Provides
    @Singleton
    fun provideYouTubeApiService(
        @ApiKeyClient client: OkHttpClient,
        moshi: Moshi,
    ): YouTubeApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(YouTubeApiService::class.java)

    @Provides
    @Singleton
    fun provideYouTubeOAuthApiService(
        @OAuthClient client: OkHttpClient,
        moshi: Moshi,
    ): YouTubeOAuthApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(YouTubeOAuthApiService::class.java)
}
