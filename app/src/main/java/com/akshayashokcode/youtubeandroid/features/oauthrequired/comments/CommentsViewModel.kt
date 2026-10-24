package com.akshayashokcode.youtubeandroid.features.oauthrequired.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.CommentThread
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentsState(
    val postState: UiState<CommentThread> = UiState.Idle,
    val deleteState: UiState<Unit> = UiState.Idle,
    val postedCommentId: String? = null,
)

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val repository: CommentsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CommentsState())
    val state: StateFlow<CommentsState> = _state.asStateFlow()

    fun postComment(videoId: String, text: String) {
        viewModelScope.launch {
            repository.postComment(videoId, text)
                .onStart { _state.value = _state.value.copy(postState = UiState.Loading) }
                .collect { result ->
                    _state.value = when (result) {
                        is AppResult.Success -> _state.value.copy(
                            postState = UiState.Data(result.data),
                            postedCommentId = result.data.snippet?.topLevelComment?.id,
                        )
                        is AppResult.Error -> _state.value.copy(
                            postState = UiState.Error(result.message.ifBlank { "Failed to post comment." }),
                        )
                    }
                }
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
                .onStart { _state.value = _state.value.copy(deleteState = UiState.Loading) }
                .collect { result ->
                    _state.value = when (result) {
                        is AppResult.Success -> _state.value.copy(
                            deleteState = UiState.Data(Unit),
                            postedCommentId = null,
                        )
                        is AppResult.Error -> _state.value.copy(
                            deleteState = UiState.Error(result.message.ifBlank { "Failed to delete comment." }),
                        )
                    }
                }
        }
    }
}
