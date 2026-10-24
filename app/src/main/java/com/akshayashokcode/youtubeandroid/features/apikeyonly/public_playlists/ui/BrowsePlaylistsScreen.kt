package com.akshayashokcode.youtubeandroid.features.apikeyonly.public_playlists.ui

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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import com.akshayashokcode.youtubeandroid.features.apikeyonly.public_playlists.BrowseMode
import com.akshayashokcode.youtubeandroid.features.apikeyonly.public_playlists.PublicPlaylistsViewModel

@Composable
fun BrowsePlaylistsScreen(
    initialPlaylistId: String? = null,
    onBackClick: () -> Unit,
    viewModel: PublicPlaylistsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var channelInput by rememberSaveable { mutableStateOf("") }
    var playlistInput by rememberSaveable { mutableStateOf(initialPlaylistId ?: "") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { OutlinedButton(onClick = onBackClick) { Text("Back") } }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Browse Playlists", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoints: playlists.list · playlistItems.list · Quota: 1 unit/request",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Column {
                BrowseMode.entries.forEach { mode ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = state.mode == mode,
                            onClick = { viewModel.setMode(mode) },
                        )
                        Text(if (mode == BrowseMode.ChannelPlaylists) "Channel playlists" else "Playlist videos",
                            modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }

        if (state.mode == BrowseMode.ChannelPlaylists) {
            item {
                OutlinedTextField(
                    value = channelInput,
                    onValueChange = { channelInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Channel ID") },
                    placeholder = { Text("UCVHFbw7woebKtfnjZbAW3GQ") },
                )
            }
            item {
                Button(
                    onClick = { viewModel.loadChannelPlaylists(channelInput) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.playlists !is UiState.Loading,
                ) { Text("Load Playlists") }
            }

            when (val ps = state.playlists) {
                UiState.Idle -> Unit
                UiState.Loading -> item { CircularProgressIndicator() }
                is UiState.Error -> item { Text(ps.message, color = MaterialTheme.colorScheme.error) }
                is UiState.Data -> {
                    val list = ps.value.items
                    if (list.isEmpty()) {
                        item { Text("No public playlists found.") }
                    } else {
                        items(list) { playlist ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    playlist.snippet?.title?.let {
                                        Text(it, style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold)
                                    }
                                    playlist.contentDetails?.itemCount?.let {
                                        Text("$it videos",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    playlist.id?.let { pid ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text("ID: $pid",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            TextButton(onClick = {
                                                playlistInput = pid
                                                viewModel.setMode(BrowseMode.PlaylistItems)
                                            }) { Text("Browse items") }
                                        }
                                    }
                                }
                            }
                        }
                        ps.value.nextPageToken?.let { token ->
                            item {
                                OutlinedButton(
                                    onClick = { viewModel.loadChannelPlaylists(channelInput, token) },
                                    modifier = Modifier.fillMaxWidth(),
                                ) { Text("Load next page") }
                            }
                        }
                    }
                }
            }
        } else {
            item {
                OutlinedTextField(
                    value = playlistInput,
                    onValueChange = { playlistInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Playlist ID") },
                )
            }
            item {
                Button(
                    onClick = { viewModel.loadPlaylistItems(playlistInput) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.items !is UiState.Loading,
                ) { Text("Load Videos") }
            }

            when (val ps = state.items) {
                UiState.Idle -> Unit
                UiState.Loading -> item { CircularProgressIndicator() }
                is UiState.Error -> item { Text(ps.message, color = MaterialTheme.colorScheme.error) }
                is UiState.Data -> {
                    val list = ps.value.items
                    if (list.isEmpty()) {
                        item { Text("No items found.") }
                    } else {
                        items(list) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    item.snippet?.title?.let {
                                        Text(it, style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold)
                                    }
                                    item.snippet?.position?.let {
                                        Text("Position: $it",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    item.contentDetails?.videoId?.let {
                                        Text("Video ID: $it",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                        ps.value.nextPageToken?.let { token ->
                            item {
                                OutlinedButton(
                                    onClick = { viewModel.loadPlaylistItems(playlistInput, token) },
                                    modifier = Modifier.fillMaxWidth(),
                                ) { Text("Load next page") }
                            }
                        }
                    }
                }
            }
        }
    }
}
