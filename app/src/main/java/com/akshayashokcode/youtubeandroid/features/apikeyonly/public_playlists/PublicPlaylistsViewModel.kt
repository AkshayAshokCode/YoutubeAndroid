package com.akshayashokcode.youtubeandroid.features.apikeyonly.public_playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.PlaylistItemListResponse
import com.akshayashokcode.youtubeandroid.core.model.PlaylistListResponse
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class BrowseMode { ChannelPlaylists, PlaylistItems }

data class BrowsePlaylistsState(
    val mode: BrowseMode = BrowseMode.ChannelPlaylists,
    val playlists: UiState<PlaylistListResponse> = UiState.Idle,
    val items: UiState<PlaylistItemListResponse> = UiState.Idle,
)

@HiltViewModel
class PublicPlaylistsViewModel @Inject constructor(
    private val repository: PublicPlaylistsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BrowsePlaylistsState())
    val state: StateFlow<BrowsePlaylistsState> = _state.asStateFlow()

    fun setMode(mode: BrowseMode) {
        _state.value = _state.value.copy(mode = mode)
    }

    fun loadChannelPlaylists(channelId: String, pageToken: String? = null) {
        if (channelId.isBlank()) {
            _state.value = _state.value.copy(playlists = UiState.Error("Enter a channel ID."))
            return
        }
        viewModelScope.launch {
            repository.getPublicPlaylistsForChannel(channelId.trim(), pageToken)
                .onStart { _state.value = _state.value.copy(playlists = UiState.Loading) }
                .collect { result ->
                    _state.value = _state.value.copy(
                        playlists = when (result) {
                            is AppResult.Success -> UiState.Data(result.data)
                            is AppResult.Error -> UiState.Error(result.message.ifBlank { "Failed to load playlists." })
                        }
                    )
                }
        }
    }

    fun loadPlaylistItems(playlistId: String, pageToken: String? = null) {
        if (playlistId.isBlank()) {
            _state.value = _state.value.copy(items = UiState.Error("Enter a playlist ID."))
            return
        }
        viewModelScope.launch {
            repository.getPlaylistItems(playlistId.trim(), pageToken)
                .onStart { _state.value = _state.value.copy(items = UiState.Loading) }
                .collect { result ->
                    _state.value = _state.value.copy(
                        items = when (result) {
                            is AppResult.Success -> UiState.Data(result.data)
                            is AppResult.Error -> UiState.Error(result.message.ifBlank { "Failed to load playlist items." })
                        }
                    )
                }
        }
    }
}
