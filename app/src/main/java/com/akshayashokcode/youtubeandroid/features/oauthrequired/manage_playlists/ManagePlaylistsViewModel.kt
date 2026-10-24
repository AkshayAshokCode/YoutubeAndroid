package com.akshayashokcode.youtubeandroid.features.oauthrequired.manage_playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.Playlist
import com.akshayashokcode.youtubeandroid.core.model.PlaylistItem
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagePlaylistsState(
    val createState: UiState<Playlist> = UiState.Idle,
    val addItemState: UiState<PlaylistItem> = UiState.Idle,
    val removeItemState: UiState<Unit> = UiState.Idle,
    val createdPlaylistId: String? = null,
    val addedItemId: String? = null,
)

@HiltViewModel
class ManagePlaylistsViewModel @Inject constructor(
    private val repository: ManagePlaylistsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ManagePlaylistsState())
    val state: StateFlow<ManagePlaylistsState> = _state.asStateFlow()

    fun createPlaylist(title: String, description: String, privacyStatus: String) {
        viewModelScope.launch {
            repository.createPlaylist(title, description, privacyStatus)
                .onStart { _state.value = _state.value.copy(createState = UiState.Loading) }
                .collect { result ->
                    _state.value = when (result) {
                        is AppResult.Success -> _state.value.copy(
                            createState = UiState.Data(result.data),
                            createdPlaylistId = result.data.id,
                        )
                        is AppResult.Error -> _state.value.copy(
                            createState = UiState.Error(result.message.ifBlank { "Failed to create playlist." }),
                        )
                    }
                }
        }
    }

    fun addVideo(playlistId: String, videoId: String) {
        viewModelScope.launch {
            repository.addVideoToPlaylist(playlistId, videoId)
                .onStart { _state.value = _state.value.copy(addItemState = UiState.Loading) }
                .collect { result ->
                    _state.value = when (result) {
                        is AppResult.Success -> _state.value.copy(
                            addItemState = UiState.Data(result.data),
                            addedItemId = result.data.id,
                        )
                        is AppResult.Error -> _state.value.copy(
                            addItemState = UiState.Error(result.message.ifBlank { "Failed to add video." }),
                        )
                    }
                }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            repository.removePlaylistItem(itemId)
                .onStart { _state.value = _state.value.copy(removeItemState = UiState.Loading) }
                .collect { result ->
                    _state.value = when (result) {
                        is AppResult.Success -> _state.value.copy(
                            removeItemState = UiState.Data(Unit),
                            addedItemId = null,
                        )
                        is AppResult.Error -> _state.value.copy(
                            removeItemState = UiState.Error(result.message.ifBlank { "Failed to remove item." }),
                        )
                    }
                }
        }
    }
}
