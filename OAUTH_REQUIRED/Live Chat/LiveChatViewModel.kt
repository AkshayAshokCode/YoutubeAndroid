package examples.oauth_required.livechat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class LiveChatViewModel(
    private val feature: LiveChat,
) : ViewModel() {
    private val _state = MutableStateFlow<LiveChatUiState>(LiveChatUiState.Idle)
    val state: StateFlow<LiveChatUiState> = _state.asStateFlow()

    fun markReady() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.flowOf(Unit)
                .onStart { _state.value = LiveChatUiState.Loading }
                .catch { _state.value = LiveChatUiState.Error(it.message ?: "OAuth feature failed") }
                .collect { _state.value = LiveChatUiState.Ready }
        }
    }
}

sealed interface LiveChatUiState {
    data object Idle : LiveChatUiState
    data object Loading : LiveChatUiState
    data object Ready : LiveChatUiState
    data class Error(val message: String) : LiveChatUiState
}
