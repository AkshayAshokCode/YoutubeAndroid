package com.akshayashokcode.youtubeandroid.core.network

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import com.akshayashokcode.youtubeandroid.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Appends the API key and identifies the app to Google.
 *
 * If the key is restricted to Android apps in Cloud Console, Google checks the
 * X-Android-Package and X-Android-Cert headers. Google's client libraries send them
 * automatically; plain REST calls (Retrofit/OkHttp) must add them, or every request
 * fails with 403 API_KEY_ANDROID_APP_BLOCKED.
 */
class ApiKeyInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
) : Interceptor {

    // SHA-1 of the signing certificate, uppercase hex without colons — the same value
    // registered in Cloud Console. Read at runtime so debug and release builds both work.
    private val certSha1: String? by lazy { signingCertSha1() }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val apiKey = BuildConfig.YOUTUBE_API_KEY

        if (apiKey.isBlank()) {
            return chain.proceed(originalRequest)
        }

        val urlWithKey = originalRequest.url.newBuilder()
            .addQueryParameter("key", apiKey)
            .build()

        val request = originalRequest.newBuilder()
            .url(urlWithKey)
            .header("X-Android-Package", context.packageName)
            .apply { certSha1?.let { header("X-Android-Cert", it) } }
            .build()

        return chain.proceed(request)
    }

    private fun signingCertSha1(): String? {
        val pm = context.packageManager
        val signature: Signature? = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                    .signingInfo?.apkContentsSigners?.firstOrNull()
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                    .signatures?.firstOrNull()
            }
        }.getOrNull()

        return signature?.let { sig ->
            MessageDigest.getInstance("SHA-1").digest(sig.toByteArray())
                .joinToString("") { "%02X".format(it) }
        }
    }
}
