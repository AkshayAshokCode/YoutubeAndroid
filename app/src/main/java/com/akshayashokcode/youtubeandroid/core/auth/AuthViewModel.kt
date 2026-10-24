package com.akshayashokcode.youtubeandroid.core.auth

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState

    private val _consentRequests = Channel<PendingIntent>(Channel.BUFFERED)

    /** One-shot consent screens the UI must launch with StartIntentSenderForResult. */
    val consentRequests: Flow<PendingIntent> = _consentRequests.receiveAsFlow()

    init {
        viewModelScope.launch { authRepository.restoreSession() }
    }

    fun signIn() {
        viewModelScope.launch {
            authRepository.signIn()?.let { _consentRequests.send(it) }
        }
    }

    fun onConsentResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            authRepository.onConsentResult(data)
        } else {
            authRepository.onConsentCancelled()
        }
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }
}
