package com.akshayashokcode.youtubeandroid.features.apikeyonly.video_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.VideoListResponse
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
class VideoDetailsViewModel @Inject constructor(
    private val repository: VideoDetailsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<VideoListResponse>>(UiState.Idle)
    val uiState: StateFlow<UiState<VideoListResponse>> = _uiState.asStateFlow()

    fun load(rawInput: String) {
        val ids = rawInput.split(",", "\n").map(String::trim).filter(String::isNotBlank)
        if (ids.isEmpty()) {
            _uiState.value = UiState.Error("Enter at least one video ID.")
            return
        }
        viewModelScope.launch {
            repository.getVideoDetails(ids)
                .onStart { _uiState.value = UiState.Loading }
                .collect { result ->
                    _uiState.value = when (result) {
                        is AppResult.Success -> UiState.Data(result.data)
                        is AppResult.Error -> UiState.Error(result.message.ifBlank { "Failed to load video details." })
                    }
                }
        }
    }
}
