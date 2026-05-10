package examples.oauth_required.postcomments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PostCommentsViewModel(
    private val feature: PostComments,
) : ViewModel() {
    private val _state = MutableStateFlow<PostCommentsUiState>(PostCommentsUiState.Idle)
    val state: StateFlow<PostCommentsUiState> = _state.asStateFlow()

    fun markReady() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.flowOf(Unit)
                .onStart { _state.value = PostCommentsUiState.Loading }
                .catch { _state.value = PostCommentsUiState.Error(it.message ?: "OAuth feature failed") }
                .collect { _state.value = PostCommentsUiState.Ready }
        }
    }
}

sealed interface PostCommentsUiState {
    data object Idle : PostCommentsUiState
    data object Loading : PostCommentsUiState
    data object Ready : PostCommentsUiState
    data class Error(val message: String) : PostCommentsUiState
}
