package com.akshayashokcode.youtubeandroid.core.auth

import android.accounts.Account
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.ClearTokenRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.RevokeAccessRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gets OAuth 2.0 access tokens for the YouTube Data API using Google Identity Services'
 * [com.google.android.gms.auth.api.identity.AuthorizationClient].
 *
 * No client ID is embedded in the app: Google matches the app to the **Android** OAuth client
 * registered in Cloud Console by package name + SHA-1 (see setup-guide/OAUTH_SETUP.md).
 *
 * Flow:
 *   1. [signIn] asks for [SCOPES]. If the user hasn't consented yet, it returns a [PendingIntent]
 *      that the UI must launch; the result comes back through [onConsentResult].
 *   2. Access tokens expire after ~1 hour. [refreshToken] fetches a new one silently and is
 *      called by [com.akshayashokcode.youtubeandroid.core.network.OAuthTokenAuthenticator] on 401.
 */
@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext context: Context,
) {

    private val authorizationClient = Identity.getAuthorizationClient(context)
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val refreshMutex = Mutex()
    private var account: Account? = null

    private val _authState = MutableStateFlow<AuthState>(AuthState.SignedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /** Returns the current Bearer token, or null when signed out. */
    val currentToken: String?
        get() = (_authState.value as? AuthState.SignedIn)?.token

    /**
     * Starts authorization. Returns a [PendingIntent] when Google needs to show the account
     * picker / consent screen, or null when the token was granted (or the request failed).
     */
    suspend fun signIn(): PendingIntent? {
        _authState.value = AuthState.SigningIn
        return try {
            val result = authorizationClient.authorize(authorizationRequest()).await()
            if (result.hasResolution()) {
                result.pendingIntent
            } else {
                onAuthorized(result)
                null
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.toAuthMessage())
            null
        }
    }

    /** Call with the data Intent returned from the consent screen launched for [signIn]. */
    fun onConsentResult(data: Intent?) {
        try {
            onAuthorized(authorizationClient.getAuthorizationResultFromIntent(data))
        } catch (e: ApiException) {
            _authState.value = AuthState.Error(e.toAuthMessage())
        }
    }

    fun onConsentCancelled() {
        _authState.value = AuthState.Error("Sign-in was cancelled.")
    }

    /**
     * Restores the session on app start without showing UI, if the user signed in previously
     * and hasn't signed out since.
     */
    suspend fun restoreSession() {
        if (prefs.getBoolean(KEY_SIGNED_IN, false)) refreshToken()
    }

    /**
     * Drops [currentToken] from Google Play services' cache and silently asks for a fresh one.
     * Returns the new token, or null when the user has to sign in again (consent revoked, etc.).
     *
     * Concurrent 401s share one refresh: callers that wait on the lock reuse the token the first
     * caller obtained instead of clearing it again.
     */
    suspend fun refreshToken(staleToken: String? = currentToken): String? = refreshMutex.withLock {
        currentToken?.let { if (it != staleToken) return@withLock it }
        staleToken?.let { clearCachedToken(it) }
        try {
            val result = authorizationClient.authorize(authorizationRequest()).await()
            if (result.hasResolution()) {
                // Consent is gone (revoked in account settings, scopes changed) — needs UI again.
                markSignedOut()
                null
            } else {
                onAuthorized(result)
                result.accessToken
            }
        } catch (e: Exception) {
            // Likely transient (offline, Play services busy): keep the saved session so the
            // next restoreSession() can try again, but stop sending the stale token.
            account = null
            _authState.value = AuthState.SignedOut
            null
        }
    }

    /** Revokes the app's access so the next sign-in shows the account picker again. */
    suspend fun signOut() {
        currentToken?.let { clearCachedToken(it) }
        account?.let { account ->
            runCatching {
                authorizationClient.revokeAccess(
                    RevokeAccessRequest.builder()
                        .setAccount(account)
                        .setScopes(SCOPES)
                        .build()
                ).await()
            }
        }
        markSignedOut()
    }

    private fun onAuthorized(result: AuthorizationResult) {
        val token = result.accessToken
        if (token == null) {
            _authState.value = AuthState.Error("Google didn't return an access token.")
            return
        }
        // Present because SCOPES includes "email" and "profile".
        val googleAccount = result.toGoogleSignInAccount()
        account = googleAccount?.account
        prefs.edit().putBoolean(KEY_SIGNED_IN, true).apply()
        _authState.value = AuthState.SignedIn(
            email = googleAccount?.email.orEmpty(),
            displayName = googleAccount?.displayName ?: "Google account",
            token = token,
        )
    }

    private suspend fun clearCachedToken(token: String) {
        runCatching {
            authorizationClient.clearToken(ClearTokenRequest.builder().setToken(token).build()).await()
        }
    }

    private fun markSignedOut() {
        account = null
        prefs.edit().putBoolean(KEY_SIGNED_IN, false).apply()
        _authState.value = AuthState.SignedOut
    }

    private fun authorizationRequest() = AuthorizationRequest.builder()
        .setRequestedScopes(SCOPES)
        .build()

    private fun Exception.toAuthMessage(): String = when {
        // DEVELOPER_ERROR (10): no Android OAuth client matches this package name + SHA-1.
        this is ApiException && statusCode == 10 ->
            "OAuth client not configured. Register an Android OAuth client for this app's " +
                "package name and SHA-1 in Google Cloud Console (see setup-guide/OAUTH_SETUP.md)."
        else -> message ?: "Sign-in failed."
    }

    companion object {
        /**
         * youtube.force-ssl authorizes every endpoint this app calls (comments, playlists,
         * subscriptions, captions, uploads, live). Narrower scopes such as youtube.upload work
         * for single features — request only what your app needs.
         *
         * Lazy because Scope's constructor touches Android framework code, which would break
         * class loading (and mocking) in plain JVM unit tests.
         */
        val SCOPES: List<Scope> by lazy {
            listOf(
                Scope("https://www.googleapis.com/auth/youtube.force-ssl"),
                Scope("email"),
                Scope("profile"),
            )
        }

        private const val PREFS_NAME = "auth"
        private const val KEY_SIGNED_IN = "signed_in"
    }
}
