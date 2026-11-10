package com.akshayashokcode.youtubeandroid.features.oauthrequired.upload_video

import android.net.Uri
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
import javax.inject.Inject

@HiltViewModel
class VideoUploadViewModel @Inject constructor(
    private val repository: VideoUploadRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Video>>(UiState.Idle)
    val uiState: StateFlow<UiState<Video>> = _uiState.asStateFlow()

    /** Fraction uploaded (0f..1f) while an upload is running, null otherwise. */
    private val _progress = MutableStateFlow<Float?>(null)
    val progress: StateFlow<Float?> = _progress.asStateFlow()

    fun upload(
        uri: Uri,
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
            repository.uploadVideo(uri, metadata)
                .onStart {
                    _uiState.value = UiState.Loading
                    _progress.value = 0f
                }
                .collect { result ->
                    when (result) {
                        is AppResult.Success -> when (val event = result.data) {
                            is UploadEvent.Progress ->
                                _progress.value = event.bytesSent.toFloat() / event.totalBytes
                            is UploadEvent.Completed -> {
                                _progress.value = null
                                _uiState.value = UiState.Data(event.video)
                            }
                        }
                        is AppResult.Error -> {
                            _progress.value = null
                            _uiState.value = UiState.Error(result.message.ifBlank { "Upload failed." })
                        }
                    }
                }
        }
    }
}
