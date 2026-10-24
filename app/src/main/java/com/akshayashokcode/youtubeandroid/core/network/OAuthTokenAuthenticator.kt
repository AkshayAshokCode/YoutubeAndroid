package com.akshayashokcode.youtubeandroid.core.network

import com.akshayashokcode.youtubeandroid.core.auth.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * Called by OkHttp when a request gets a 401. Access tokens expire after ~1 hour, so this
 * fetches a fresh one silently and retries the request once.
 */
class OAuthTokenAuthenticator @Inject constructor(
    private val authRepository: AuthRepository,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Already retried with a fresh token and still 401 — give up.
        if (response.priorResponse != null) return null

        val staleToken = response.request.header("Authorization")?.removePrefix("Bearer ")
        // OkHttp calls this on a background thread, so blocking here is fine.
        val freshToken = runBlocking { authRepository.refreshToken(staleToken) } ?: return null
        return response.request.newBuilder()
            .header("Authorization", "Bearer $freshToken")
            .build()
    }
}
