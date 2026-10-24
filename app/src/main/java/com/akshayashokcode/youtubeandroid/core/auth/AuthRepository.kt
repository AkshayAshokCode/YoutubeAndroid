package com.akshayashokcode.youtubeandroid.core.auth

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub auth repository. Replace [signIn] with a real GoogleSignInClient flow
 * and [currentToken] with your token store once you have OAuth credentials.
 *
 * To integrate:
 *   1. Add your OAuth 2.0 Web-Client-ID to res/values/strings.xml
 *   2. Build a GoogleSignInOptions with the client ID + requestServerAuthCode
 *   3. Launch the sign-in intent from an Activity/Fragment and pass the resulting
 *      account back to this repository.
 */
@Singleton
class AuthRepository @Inject constructor() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.SignedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /** Returns the current Bearer token, or null when signed out. */
    val currentToken: String?
        get() = (_authState.value as? AuthState.SignedIn)?.token

    /** Demo sign-in — replace with real Google Sign-In. */
    suspend fun signIn() {
        _authState.value = AuthState.SigningIn
        delay(600)
        _authState.value = AuthState.SignedIn(
            email = "demo@example.com",
            displayName = "Demo User",
            // Replace with the real token obtained from GoogleSignInAccount.serverAuthCode
            token = "REPLACE_WITH_REAL_OAUTH_TOKEN",
        )
    }

    fun signOut() {
        _authState.value = AuthState.SignedOut
    }
}
