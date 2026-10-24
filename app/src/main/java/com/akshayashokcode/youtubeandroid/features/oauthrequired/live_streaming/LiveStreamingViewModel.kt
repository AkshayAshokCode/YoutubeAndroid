package com.akshayashokcode.youtubeandroid.features.oauthrequired.live_streaming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.LiveBroadcast
import com.akshayashokcode.youtubeandroid.core.model.LiveStream
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveStreamingState(
    val broadcastState: UiState<LiveBroadcast> = UiState.Idle,
    val streamState: UiState<LiveStream> = UiState.Idle,
    val transitionState: UiState<LiveBroadcast> = UiState.Idle,
    val currentBroadcastId: String? = null,
    val currentStreamId: String? = null,
    val currentLifeCycleStatus: String? = null,
)

@HiltViewModel
class LiveStreamingViewModel @Inject constructor(
    private val repository: LiveStreamingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LiveStreamingState())
    val state: StateFlow<LiveStreamingState> = _state.asStateFlow()

    fun createBroadcast(title: String, startTime: String, description: String, privacy: String) {
        viewModelScope.launch {
            repository.createBroadcast(title, startTime, description.ifBlank { null }, privacy)
                .onStart { _state.value = _state.value.copy(broadcastState = UiState.Loading) }
                .collect { result ->
                    _state.value = when (result) {
                        is AppResult.Success -> _state.value.copy(
                            broadcastState = UiState.Data(result.data),
                            currentBroadcastId = result.data.id,
                            currentLifeCycleStatus = result.data.status?.lifeCycleStatus,
                        )
                        is AppResult.Error -> _state.value.copy(
                            broadcastState = UiState.Error(result.message.ifBlank { "Failed to create broadcast." }),
                        )
                    }
                }
        }
    }

    fun createStream(title: String, resolution: String, frameRate: String) {
        viewModelScope.launch {
            repository.createStream(title, resolution, frameRate)
                .onStart { _state.value = _state.value.copy(streamState = UiState.Loading) }
                .collect { result ->
                    _state.value = when (result) {
                        is AppResult.Success -> _state.value.copy(
                            streamState = UiState.Data(result.data),
                            currentStreamId = result.data.id,
                        )
                        is AppResult.Error -> _state.value.copy(
                            streamState = UiState.Error(result.message.ifBlank { "Failed to create stream." }),
                        )
                    }
                }
        }
    }

    fun bindAndTransition(targetStatus: String) {
        val broadcastId = _state.value.currentBroadcastId ?: return
        val streamId = _state.value.currentStreamId

        viewModelScope.launch {
            if (streamId != null) {
                repository.bindBroadcastToStream(broadcastId, streamId).collect { }
            }
            repository.transitionBroadcast(broadcastId, targetStatus)
                .onStart { _state.value = _state.value.copy(transitionState = UiState.Loading) }
                .collect { result ->
                    _state.value = when (result) {
                        is AppResult.Success -> _state.value.copy(
                            transitionState = UiState.Data(result.data),
                            currentLifeCycleStatus = result.data.status?.lifeCycleStatus,
                        )
                        is AppResult.Error -> _state.value.copy(
                            transitionState = UiState.Error(result.message.ifBlank { "Transition failed." }),
                        )
                    }
                }
        }
    }
}
