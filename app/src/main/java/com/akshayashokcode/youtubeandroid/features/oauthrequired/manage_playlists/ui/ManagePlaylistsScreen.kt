package com.akshayashokcode.youtubeandroid.features.oauthrequired.manage_playlists.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.akshayashokcode.youtubeandroid.features.oauthrequired.manage_playlists.ManagePlaylistsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePlaylistsScreen(
    onBackClick: () -> Unit,
    viewModel: ManagePlaylistsViewModel = hiltViewModel(),
) {
    var playlistTitle by rememberSaveable { mutableStateOf("") }
    var playlistDesc by rememberSaveable { mutableStateOf("") }
    var privacyExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedPrivacy by rememberSaveable { mutableStateOf("private") }

    var playlistId by rememberSaveable { mutableStateOf("") }
    var videoId by rememberSaveable { mutableStateOf("") }
    var itemId by rememberSaveable { mutableStateOf("") }

    val state by viewModel.state.collectAsState()
    val privacyOptions = listOf("private", "public", "unlisted")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { OutlinedButton(onClick = onBackClick) { Text("Back") } }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Manage Playlists", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoints: playlists.insert · playlistItems.*\nScope: youtube · Quota: 50 units/write",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Create playlist
        item {
            Text("Create Playlist", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }
        item {
            OutlinedTextField(value = playlistTitle, onValueChange = { playlistTitle = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Title") })
        }
        item {
            OutlinedTextField(value = playlistDesc, onValueChange = { playlistDesc = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Description (optional)") })
        }
        item {
            ExposedDropdownMenuBox(expanded = privacyExpanded,
                onExpandedChange = { privacyExpanded = !privacyExpanded }) {
                OutlinedTextField(
                    value = selectedPrivacy,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Privacy") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(privacyExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                ExposedDropdownMenu(expanded = privacyExpanded,
                    onDismissRequest = { privacyExpanded = false }) {
                    privacyOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = { selectedPrivacy = opt; privacyExpanded = false },
                        )
                    }
                }
            }
        }
        item {
            Button(onClick = { viewModel.createPlaylist(playlistTitle, playlistDesc, selectedPrivacy) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.createState !is UiState.Loading) { Text("Create Playlist") }
        }
        when (val s = state.createState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            is UiState.Data -> item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Created!", color = MaterialTheme.colorScheme.primary)
                    state.createdPlaylistId?.let {
                        Text("Playlist ID: $it", style = MaterialTheme.typography.bodySmall)
                        Text("Copy this ID into the 'Add Video' field below.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else -> Unit
        }

        item { Divider() }

        // Add video to playlist
        item {
            Text("Add Video to Playlist", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }
        item {
            OutlinedTextField(value = playlistId, onValueChange = { playlistId = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Playlist ID") })
        }
        item {
            OutlinedTextField(value = videoId, onValueChange = { videoId = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Video ID") })
        }
        item {
            Button(onClick = { viewModel.addVideo(playlistId, videoId) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.addItemState !is UiState.Loading) { Text("Add Video") }
        }
        when (val s = state.addItemState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            is UiState.Data -> item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Added — Item ID: ${state.addedItemId}",
                        color = MaterialTheme.colorScheme.primary)
                    state.addedItemId?.let { iid ->
                        itemId = iid
                        Button(
                            onClick = { viewModel.removeItem(iid) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            ),
                        ) { Text("Remove this item") }
                    }
                }
            }
            else -> Unit
        }

        when (val s = state.removeItemState) {
            is UiState.Data -> item { Text("Item removed.", color = MaterialTheme.colorScheme.primary) }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            else -> Unit
        }
    }
}
