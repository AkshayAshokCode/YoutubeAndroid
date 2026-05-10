package examples.oauth_required.managecaptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ManageCaptionsViewModel(
    private val feature: ManageCaptions,
) : ViewModel() {
    private val _state = MutableStateFlow<ManageCaptionsUiState>(ManageCaptionsUiState.Idle)
    val state: StateFlow<ManageCaptionsUiState> = _state.asStateFlow()

    fun markReady() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.flowOf(Unit)
                .onStart { _state.value = ManageCaptionsUiState.Loading }
                .catch { _state.value = ManageCaptionsUiState.Error(it.message ?: "OAuth feature failed") }
                .collect { _state.value = ManageCaptionsUiState.Ready }
        }
    }
}

sealed interface ManageCaptionsUiState {
    data object Idle : ManageCaptionsUiState
    data object Loading : ManageCaptionsUiState
    data object Ready : ManageCaptionsUiState
    data class Error(val message: String) : ManageCaptionsUiState
}
