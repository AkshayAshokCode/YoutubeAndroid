package examples.api_key_only.getchannelinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class GetChannelInfoViewModel(
    private val getChannelInfo: GetChannelInfo,
) : ViewModel() {
    private val _state = MutableStateFlow<GetChannelInfoUiState>(GetChannelInfoUiState.Idle)
    val state: StateFlow<GetChannelInfoUiState> = _state.asStateFlow()

    fun load(channelIdText: String) {
        viewModelScope.launch {
            getChannelInfo.execute(channelIdText.split(",", "
"))
                .onStart { _state.value = GetChannelInfoUiState.Loading }
                .catch { _state.value = GetChannelInfoUiState.Error(it.message ?: "Could not load channel") }
                .collect { _state.value = GetChannelInfoUiState.Content(it) }
        }
    }
}

sealed interface GetChannelInfoUiState {
    data object Idle : GetChannelInfoUiState
    data object Loading : GetChannelInfoUiState
    data class Content(val channels: List<ChannelInfo>) : GetChannelInfoUiState
    data class Error(val message: String) : GetChannelInfoUiState
}
