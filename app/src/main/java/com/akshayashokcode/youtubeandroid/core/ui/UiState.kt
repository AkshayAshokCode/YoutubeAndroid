package com.akshayashokcode.youtubeandroid.core.ui

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Data<T>(val value: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
