package examples.oauth_required.uploadvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class UploadVideoViewModel(
    private val feature: UploadVideo,
) : ViewModel() {
    private val _state = MutableStateFlow<UploadVideoUiState>(UploadVideoUiState.Idle)
    val state: StateFlow<UploadVideoUiState> = _state.asStateFlow()

    fun markReady() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.flowOf(Unit)
                .onStart { _state.value = UploadVideoUiState.Loading }
                .catch { _state.value = UploadVideoUiState.Error(it.message ?: "OAuth feature failed") }
                .collect { _state.value = UploadVideoUiState.Ready }
        }
    }
}

sealed interface UploadVideoUiState {
    data object Idle : UploadVideoUiState
    data object Loading : UploadVideoUiState
    data object Ready : UploadVideoUiState
    data class Error(val message: String) : UploadVideoUiState
}
