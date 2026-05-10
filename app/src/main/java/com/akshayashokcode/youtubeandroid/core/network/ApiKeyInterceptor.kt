package com.akshayashokcode.youtubeandroid.core.network

import com.akshayashokcode.youtubeandroid.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiKeyInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val apiKey = BuildConfig.YOUTUBE_API_KEY

        if (apiKey.isBlank()) {
            return chain.proceed(originalRequest)
        }

        val urlWithKey = originalRequest.url.newBuilder()
            .addQueryParameter("key", apiKey)
            .build()

        return chain.proceed(originalRequest.newBuilder().url(urlWithKey).build())
    }
}
