package com.akshayashokcode.youtubeandroid.features.apikeyonly.video_details.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import com.akshayashokcode.youtubeandroid.features.apikeyonly.video_details.VideoDetailsViewModel

@Composable
fun VideoDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: VideoDetailsViewModel = hiltViewModel(),
) {
    var input by rememberSaveable { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { OutlinedButton(onClick = onBackClick) { Text("Back") } }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Video Details", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoint: videos.list · Quota: 1 unit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Text("Fetch full metadata for one or more videos.",
                style = MaterialTheme.typography.bodyLarge)
        }

        item {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Video ID(s)") },
                placeholder = { Text("dQw4w9WgXcQ, or one per line") },
                minLines = 2,
            )
        }

        item {
            Button(
                onClick = { viewModel.load(input) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is UiState.Loading,
            ) { Text("Fetch Details") }
        }

        when (val state = uiState) {
            UiState.Idle -> Unit
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
            is UiState.Data -> {
                val videos = state.value.items
                if (videos.isEmpty()) {
                    item { Text("No videos found for the given ID(s).") }
                } else {
                    items(videos) { video ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                video.snippet?.title?.let {
                                    Text(it, style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold)
                                }
                                video.snippet?.channelTitle?.let {
                                    Text(it, style = MaterialTheme.typography.bodyMedium)
                                }
                                video.contentDetails?.duration?.let {
                                    LabelRow("Duration", it)
                                }
                                video.statistics?.viewCount?.let { LabelRow("Views", it) }
                                video.statistics?.likeCount?.let { LabelRow("Likes", it) }
                                video.statistics?.commentCount?.let { LabelRow("Comments", it) }
                                video.status?.privacyStatus?.let { LabelRow("Privacy", it) }
                                video.status?.embeddable?.let { LabelRow("Embeddable", it.toString()) }
                                video.liveStreamingDetails?.activeLiveChatId?.let {
                                    LabelRow("Live Chat ID", it)
                                }
                                video.id?.let {
                                    Text("ID: $it",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelRow(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$label:", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}
