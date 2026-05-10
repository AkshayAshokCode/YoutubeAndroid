package examples.api_key_only.getvideodetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class GetVideoDetailsViewModel(
    private val getVideoDetails: GetVideoDetails,
) : ViewModel() {
    private val _state = MutableStateFlow<GetVideoDetailsUiState>(GetVideoDetailsUiState.Idle)
    val state: StateFlow<GetVideoDetailsUiState> = _state.asStateFlow()

    fun load(videoIdText: String) {
        val ids = videoIdText.split(",", "
").map(String::trim)
        viewModelScope.launch {
            getVideoDetails.execute(ids)
                .onStart { _state.value = GetVideoDetailsUiState.Loading }
                .catch { _state.value = GetVideoDetailsUiState.Error(it.message ?: "Could not load video details") }
                .collect { _state.value = GetVideoDetailsUiState.Content(it) }
        }
    }
}

sealed interface GetVideoDetailsUiState {
    data object Idle : GetVideoDetailsUiState
    data object Loading : GetVideoDetailsUiState
    data class Content(val videos: List<VideoDetails>) : GetVideoDetailsUiState
    data class Error(val message: String) : GetVideoDetailsUiState
}
