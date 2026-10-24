package com.akshayashokcode.youtubeandroid.features.oauthrequired.upload_video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.Video
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VideoUploadViewModel @Inject constructor(
    private val repository: VideoUploadRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Video>>(UiState.Idle)
    val uiState: StateFlow<UiState<Video>> = _uiState.asStateFlow()

    fun upload(
        file: File,
        title: String,
        description: String,
        tags: String,
        categoryId: String,
        privacyStatus: String,
        madeForKids: Boolean,
    ) {
        val metadata = VideoMetadata(
            title = title,
            description = description,
            tags = tags.split(",").map(String::trim).filter(String::isNotBlank),
            categoryId = categoryId.ifBlank { "22" },
            privacyStatus = privacyStatus,
            madeForKids = madeForKids,
        )
        viewModelScope.launch {
            repository.uploadVideo(file, metadata)
                .onStart { _uiState.value = UiState.Loading }
                .collect { result ->
                    _uiState.value = when (result) {
                        is AppResult.Success -> UiState.Data(result.data)
                        is AppResult.Error -> UiState.Error(result.message.ifBlank { "Upload failed." })
                    }
                }
        }
    }
}
