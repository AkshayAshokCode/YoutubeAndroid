package examples.oauth_required.manageplaylists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ManagePlaylistsViewModel(
    private val feature: ManagePlaylists,
) : ViewModel() {
    private val _state = MutableStateFlow<ManagePlaylistsUiState>(ManagePlaylistsUiState.Idle)
    val state: StateFlow<ManagePlaylistsUiState> = _state.asStateFlow()

    fun markReady() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.flowOf(Unit)
                .onStart { _state.value = ManagePlaylistsUiState.Loading }
                .catch { _state.value = ManagePlaylistsUiState.Error(it.message ?: "OAuth feature failed") }
                .collect { _state.value = ManagePlaylistsUiState.Ready }
        }
    }
}

sealed interface ManagePlaylistsUiState {
    data object Idle : ManagePlaylistsUiState
    data object Loading : ManagePlaylistsUiState
    data object Ready : ManagePlaylistsUiState
    data class Error(val message: String) : ManagePlaylistsUiState
}
