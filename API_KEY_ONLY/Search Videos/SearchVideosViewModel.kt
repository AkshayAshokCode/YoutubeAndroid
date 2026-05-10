package examples.api_key_only.searchvideos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SearchVideosViewModel(
    private val searchVideos: SearchVideos,
) : ViewModel() {
    private val _state = MutableStateFlow<SearchVideosUiState>(SearchVideosUiState.Idle)
    val state: StateFlow<SearchVideosUiState> = _state.asStateFlow()

    fun search(query: String, type: SearchResultType = SearchResultType.Video) {
        viewModelScope.launch {
            searchVideos.execute(query = query, type = type)
                .onStart { _state.value = SearchVideosUiState.Loading }
                .catch { throwable ->
                    _state.value = SearchVideosUiState.Error(throwable.message ?: "Search failed")
                }
                .collect { result ->
                    when (result) {
                        is SearchVideosResult.Success -> _state.value = SearchVideosUiState.Content(
                            items = result.items,
                            nextPageToken = result.nextPageToken,
                        )
                    }
                }
        }
    }
}

sealed interface SearchVideosUiState {
    data object Idle : SearchVideosUiState
    data object Loading : SearchVideosUiState
    data class Content(
        val items: List<SearchItem>,
        val nextPageToken: String?,
    ) : SearchVideosUiState
    data class Error(val message: String) : SearchVideosUiState
}
