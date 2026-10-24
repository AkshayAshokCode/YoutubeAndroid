package com.akshayashokcode.youtubeandroid.features.apikeyonly.channel_info.ui

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
import androidx.compose.material3.TextButton
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
import com.akshayashokcode.youtubeandroid.features.apikeyonly.channel_info.ChannelInfoViewModel

@Composable
fun ChannelInfoScreen(
    onBackClick: () -> Unit,
    onBrowsePlaylistsClick: (uploadsPlaylistId: String) -> Unit,
    viewModel: ChannelInfoViewModel = hiltViewModel(),
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
                Text("Channel Info", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoint: channels.list · Quota: 1 unit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Text("Fetch metadata and statistics for one or more YouTube channels.",
                style = MaterialTheme.typography.bodyLarge)
        }

        item {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Channel ID(s)") },
                placeholder = { Text("UCVHFbw7woebKtfnjZbAW3GQ") },
                minLines = 2,
            )
        }

        item {
            Button(
                onClick = { viewModel.load(input) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is UiState.Loading,
            ) { Text("Fetch Channel Info") }
        }

        when (val state = uiState) {
            UiState.Idle -> Unit
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
            is UiState.Data -> {
                val channels = state.value.items
                if (channels.isEmpty()) {
                    item { Text("No channels found.") }
                } else {
                    items(channels) { channel ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                channel.snippet?.title?.let {
                                    Text(it, style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold)
                                }
                                channel.snippet?.description?.let {
                                    if (it.isNotBlank()) Text(it.take(120) + if (it.length > 120) "…" else "",
                                        style = MaterialTheme.typography.bodySmall)
                                }
                                channel.statistics?.subscriberCount?.let { LabelRow("Subscribers", it) }
                                channel.statistics?.videoCount?.let { LabelRow("Videos", it) }
                                channel.statistics?.viewCount?.let { LabelRow("Total views", it) }
                                channel.contentDetails?.relatedPlaylists?.uploads?.let { uploadsId ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        LabelRow("Uploads playlist", uploadsId)
                                        TextButton(onClick = { onBrowsePlaylistsClick(uploadsId) }) {
                                            Text("Browse")
                                        }
                                    }
                                }
                                channel.id?.let {
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
