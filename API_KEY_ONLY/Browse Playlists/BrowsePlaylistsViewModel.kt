package examples.api_key_only.browseplaylists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BrowsePlaylistsViewModel(private val browsePlaylists: BrowsePlaylists) : ViewModel() {
    private val _state = MutableStateFlow<BrowsePlaylistsUiState>(BrowsePlaylistsUiState.Idle)
    val state: StateFlow<BrowsePlaylistsUiState> = _state.asStateFlow()

    fun loadChannelPlaylists(channelId: String) = viewModelScope.launch {
        browsePlaylists.channelPlaylists(channelId)
            .onStart { _state.value = BrowsePlaylistsUiState.Loading }
            .catch { _state.value = BrowsePlaylistsUiState.Error(it.message ?: "Could not load playlists") }
            .collect { _state.value = BrowsePlaylistsUiState.Playlists(it.items, it.nextPageToken) }
    }

    fun loadPlaylistVideos(playlistId: String) = viewModelScope.launch {
        browsePlaylists.playlistVideos(playlistId)
            .onStart { _state.value = BrowsePlaylistsUiState.Loading }
            .catch { _state.value = BrowsePlaylistsUiState.Error(it.message ?: "Could not load playlist videos") }
            .collect { _state.value = BrowsePlaylistsUiState.Videos(it.items, it.nextPageToken) }
    }
}

sealed interface BrowsePlaylistsUiState {
    data object Idle : BrowsePlaylistsUiState
    data object Loading : BrowsePlaylistsUiState
    data class Playlists(val playlists: List<Playlist>, val nextPageToken: String?) : BrowsePlaylistsUiState
    data class Videos(val videos: List<PlaylistVideo>, val nextPageToken: String?) : BrowsePlaylistsUiState
    data class Error(val message: String) : BrowsePlaylistsUiState
}
