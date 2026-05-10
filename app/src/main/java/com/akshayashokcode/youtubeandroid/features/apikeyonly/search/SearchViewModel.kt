package com.akshayashokcode.youtubeandroid.features.apikeyonly.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.SearchListResponse
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<SearchListResponse>>(UiState.Idle)
    val uiState: StateFlow<UiState<SearchListResponse>> = _uiState.asStateFlow()

    fun search(query: String, type: SearchType? = null) {
        if (query.isBlank()) {
            _uiState.value = UiState.Error("Enter a search query.")
            return
        }

        viewModelScope.launch {
            repository.search(query = query.trim(), type = type)
                .onStart { _uiState.value = UiState.Loading }
                .collect { result ->
                    _uiState.value = when (result) {
                        is AppResult.Success -> UiState.Data(result.data)
                        is AppResult.Error -> UiState.Error(result.message.ifBlank { "Search failed." })
                    }
                }
        }
    }
}
