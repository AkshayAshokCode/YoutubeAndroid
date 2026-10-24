package com.akshayashokcode.youtubeandroid.features.oauthrequired.captions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.CaptionListResponse
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

data class CaptionsState(
    val listState: UiState<CaptionListResponse> = UiState.Idle,
    val uploadState: UiState<Unit> = UiState.Idle,
    val deleteState: UiState<Unit> = UiState.Idle,
)

@HiltViewModel
class CaptionsViewModel @Inject constructor(
    private val repository: CaptionsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CaptionsState())
    val state: StateFlow<CaptionsState> = _state.asStateFlow()

    fun listCaptions(videoId: String) {
        viewModelScope.launch {
            repository.listCaptions(videoId)
                .onStart { _state.value = _state.value.copy(listState = UiState.Loading) }
                .collect { result ->
                    _state.value = _state.value.copy(
                        listState = when (result) {
                            is AppResult.Success -> UiState.Data(result.data)
                            is AppResult.Error -> UiState.Error(result.message.ifBlank { "Failed to load captions." })
                        }
                    )
                }
        }
    }

    fun uploadCaption(videoId: String, language: String, name: String, file: File) {
        viewModelScope.launch {
            repository.uploadCaption(videoId, language, name, file)
                .onStart { _state.value = _state.value.copy(uploadState = UiState.Loading) }
                .collect { result ->
                    _state.value = _state.value.copy(
                        uploadState = when (result) {
                            is AppResult.Success -> UiState.Data(Unit)
                            is AppResult.Error -> UiState.Error(result.message.ifBlank { "Failed to upload caption." })
                        }
                    )
                }
        }
    }

    fun deleteCaption(captionId: String) {
        viewModelScope.launch {
            repository.deleteCaption(captionId)
                .onStart { _state.value = _state.value.copy(deleteState = UiState.Loading) }
                .collect { result ->
                    _state.value = _state.value.copy(
                        deleteState = when (result) {
                            is AppResult.Success -> UiState.Data(Unit)
                            is AppResult.Error -> UiState.Error(result.message.ifBlank { "Failed to delete caption." })
                        }
                    )
                }
        }
    }
}
