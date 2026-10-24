package com.akshayashokcode.youtubeandroid.features.oauthrequired.live_chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.LiveChatMessage
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveChatState(
    val messages: List<LiveChatMessage> = emptyList(),
    val fetchState: UiState<Unit> = UiState.Idle,
    val sendState: UiState<LiveChatMessage> = UiState.Idle,
    val pollingIntervalMs: Long = 5_000L,
    val isPolling: Boolean = false,
    val nextPageToken: String? = null,
)

@HiltViewModel
class LiveChatViewModel @Inject constructor(
    private val repository: LiveChatRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LiveChatState())
    val state: StateFlow<LiveChatState> = _state.asStateFlow()

    private var pollingJob: Job? = null

    fun startPolling(liveChatId: String) {
        if (_state.value.isPolling) return
        _state.value = _state.value.copy(isPolling = true)
        pollingJob = viewModelScope.launch {
            while (true) {
                fetchMessages(liveChatId)
                delay(_state.value.pollingIntervalMs)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        _state.value = _state.value.copy(isPolling = false)
    }

    private suspend fun fetchMessages(liveChatId: String) {
        repository.getMessages(liveChatId, _state.value.nextPageToken)
            .onStart { _state.value = _state.value.copy(fetchState = UiState.Loading) }
            .collect { result ->
                when (result) {
                    is AppResult.Success -> {
                        val response = result.data
                        _state.value = _state.value.copy(
                            messages = (_state.value.messages + response.items).takeLast(200),
                            fetchState = UiState.Data(Unit),
                            pollingIntervalMs = response.pollingIntervalMillis ?: 5_000L,
                            nextPageToken = response.nextPageToken,
                        )
                    }
                    is AppResult.Error -> _state.value = _state.value.copy(
                        fetchState = UiState.Error(result.message.ifBlank { "Failed to fetch messages." }),
                    )
                }
            }
    }

    fun sendMessage(liveChatId: String, text: String) {
        viewModelScope.launch {
            repository.sendMessage(liveChatId, text)
                .onStart { _state.value = _state.value.copy(sendState = UiState.Loading) }
                .collect { result ->
                    _state.value = _state.value.copy(
                        sendState = when (result) {
                            is AppResult.Success -> UiState.Data(result.data)
                            is AppResult.Error -> UiState.Error(result.message.ifBlank { "Failed to send message." })
                        }
                    )
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
