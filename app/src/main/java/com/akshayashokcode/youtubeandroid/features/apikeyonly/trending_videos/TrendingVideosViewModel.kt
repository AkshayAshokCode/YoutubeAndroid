package com.akshayashokcode.youtubeandroid.features.apikeyonly.trending_videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.VideoListResponse
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import com.akshayashokcode.youtubeandroid.features.apikeyonly.video_details.VideoDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingVideosViewModel @Inject constructor(
    private val repository: VideoDetailsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<VideoListResponse>>(UiState.Idle)
    val uiState: StateFlow<UiState<VideoListResponse>> = _uiState.asStateFlow()

    fun load(regionCode: String, categoryId: String?, pageToken: String? = null) {
        val code = regionCode.trim().ifBlank { "US" }
        val catId = categoryId?.trim()?.ifBlank { null }
        viewModelScope.launch {
            repository.getPopularVideos(
                regionCode = code,
                videoCategoryId = catId,
                pageToken = pageToken,
            )
                .onStart { _uiState.value = UiState.Loading }
                .collect { result ->
                    _uiState.value = when (result) {
                        is AppResult.Success -> UiState.Data(result.data)
                        is AppResult.Error -> UiState.Error(result.message.ifBlank { "Failed to load trending videos." })
                    }
                }
        }
    }
}
