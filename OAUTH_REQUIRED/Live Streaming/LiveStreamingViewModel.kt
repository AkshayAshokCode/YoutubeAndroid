package examples.oauth_required.livestreaming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class LiveStreamingViewModel(
    private val feature: LiveStreaming,
) : ViewModel() {
    private val _state = MutableStateFlow<LiveStreamingUiState>(LiveStreamingUiState.Idle)
    val state: StateFlow<LiveStreamingUiState> = _state.asStateFlow()

    fun markReady() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.flowOf(Unit)
                .onStart { _state.value = LiveStreamingUiState.Loading }
                .catch { _state.value = LiveStreamingUiState.Error(it.message ?: "OAuth feature failed") }
                .collect { _state.value = LiveStreamingUiState.Ready }
        }
    }
}

sealed interface LiveStreamingUiState {
    data object Idle : LiveStreamingUiState
    data object Loading : LiveStreamingUiState
    data object Ready : LiveStreamingUiState
    data class Error(val message: String) : LiveStreamingUiState
}
