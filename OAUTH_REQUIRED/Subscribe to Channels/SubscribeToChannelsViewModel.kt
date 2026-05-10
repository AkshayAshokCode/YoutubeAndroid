package examples.oauth_required.subscribetochannels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SubscribeToChannelsViewModel(
    private val feature: SubscribeToChannels,
) : ViewModel() {
    private val _state = MutableStateFlow<SubscribeToChannelsUiState>(SubscribeToChannelsUiState.Idle)
    val state: StateFlow<SubscribeToChannelsUiState> = _state.asStateFlow()

    fun markReady() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.flowOf(Unit)
                .onStart { _state.value = SubscribeToChannelsUiState.Loading }
                .catch { _state.value = SubscribeToChannelsUiState.Error(it.message ?: "OAuth feature failed") }
                .collect { _state.value = SubscribeToChannelsUiState.Ready }
        }
    }
}

sealed interface SubscribeToChannelsUiState {
    data object Idle : SubscribeToChannelsUiState
    data object Loading : SubscribeToChannelsUiState
    data object Ready : SubscribeToChannelsUiState
    data class Error(val message: String) : SubscribeToChannelsUiState
}
