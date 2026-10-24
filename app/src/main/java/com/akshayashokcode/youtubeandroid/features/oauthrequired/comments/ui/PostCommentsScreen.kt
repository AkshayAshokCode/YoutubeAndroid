package com.akshayashokcode.youtubeandroid.features.oauthrequired.comments.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import com.akshayashokcode.youtubeandroid.features.oauthrequired.comments.CommentsViewModel

@Composable
fun PostCommentsScreen(
    onBackClick: () -> Unit,
    viewModel: CommentsViewModel = hiltViewModel(),
) {
    var videoId by rememberSaveable { mutableStateOf("") }
    var commentText by rememberSaveable { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { OutlinedButton(onClick = onBackClick) { Text("Back") } }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Post Comments", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoints: commentThreads.insert · comments.delete\nScope: youtube.force-ssl · Quota: 50 units/write",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            OutlinedTextField(
                value = videoId,
                onValueChange = { videoId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Video ID") },
                placeholder = { Text("dQw4w9WgXcQ") },
            )
        }

        item {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Comment") },
                placeholder = { Text("Great video!") },
                minLines = 3,
            )
        }

        item {
            Button(
                onClick = { viewModel.postComment(videoId, commentText) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.postState !is UiState.Loading,
            ) { Text("Post Comment") }
        }

        when (val s = state.postState) {
            UiState.Idle -> Unit
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            is UiState.Data -> {
                item {
                    Text("Comment posted!", color = MaterialTheme.colorScheme.primary)
                }
                state.postedCommentId?.let { cid ->
                    item {
                        Text("Comment / thread ID: $cid",
                            style = MaterialTheme.typography.bodySmall)
                    }
                    item {
                        Button(
                            onClick = { viewModel.deleteComment(cid) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            ),
                            enabled = state.deleteState !is UiState.Loading,
                        ) { Text("Delete this comment") }
                    }
                }
            }
        }

        when (val s = state.deleteState) {
            is UiState.Data -> item { Text("Comment deleted.", color = MaterialTheme.colorScheme.primary) }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            else -> Unit
        }
    }
}
