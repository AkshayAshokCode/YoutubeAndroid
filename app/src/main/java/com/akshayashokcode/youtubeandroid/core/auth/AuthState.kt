package com.akshayashokcode.youtubeandroid.core.auth

sealed interface AuthState {
    data object SignedOut : AuthState
    data object SigningIn : AuthState
    data class SignedIn(
        val email: String,
        val displayName: String,
        val token: String,
    ) : AuthState
}
