package com.akshayashokcode.youtubeandroid.core.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState

    fun signIn() {
        viewModelScope.launch { authRepository.signIn() }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
