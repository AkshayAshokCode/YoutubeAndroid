package com.akshayashokcode.youtubeandroid.features.oauthrequired.live_streaming.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.akshayashokcode.youtubeandroid.features.oauthrequired.live_streaming.LiveStreamingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamingScreen(
    onBackClick: () -> Unit,
    viewModel: LiveStreamingViewModel = hiltViewModel(),
) {
    var broadcastTitle by rememberSaveable { mutableStateOf("") }
    var startTime by rememberSaveable { mutableStateOf("") }
    var broadcastDesc by rememberSaveable { mutableStateOf("") }
    var privacyExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedPrivacy by rememberSaveable { mutableStateOf("private") }
    var streamTitle by rememberSaveable { mutableStateOf("") }
    var streamKeyVisible by rememberSaveable { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()
    val privacyOptions = listOf("private", "public", "unlisted")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { OutlinedButton(onClick = onBackClick) { Text("Back") } }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Live Streaming", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoints: liveBroadcasts.* · liveStreams.*\nScope: youtube · Quota: 50+ units",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Create Broadcast
        item {
            Text("1. Create Broadcast", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }
        item {
            OutlinedTextField(value = broadcastTitle, onValueChange = { broadcastTitle = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Broadcast title") })
        }
        item {
            OutlinedTextField(value = startTime, onValueChange = { startTime = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Scheduled start (ISO 8601)") },
                placeholder = { Text("2025-12-31T20:00:00Z") })
        }
        item {
            OutlinedTextField(value = broadcastDesc, onValueChange = { broadcastDesc = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Description (optional)") })
        }
        item {
            ExposedDropdownMenuBox(expanded = privacyExpanded,
                onExpandedChange = { privacyExpanded = !privacyExpanded }) {
                OutlinedTextField(
                    value = selectedPrivacy, onValueChange = {}, readOnly = true,
                    label = { Text("Privacy") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(privacyExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                ExposedDropdownMenu(expanded = privacyExpanded,
                    onDismissRequest = { privacyExpanded = false }) {
                    privacyOptions.forEach { opt ->
                        DropdownMenuItem(text = { Text(opt) },
                            onClick = { selectedPrivacy = opt; privacyExpanded = false })
                    }
                }
            }
        }
        item {
            Button(onClick = { viewModel.createBroadcast(broadcastTitle, startTime, broadcastDesc, selectedPrivacy) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.broadcastState !is UiState.Loading) { Text("Create Broadcast") }
        }
        when (val s = state.broadcastState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            is UiState.Data -> item {
                Text("Broadcast ID: ${state.currentBroadcastId}",
                    color = MaterialTheme.colorScheme.primary)
            }
            else -> Unit
        }

        item { Divider() }

        // Create Stream
        item {
            Text("2. Create Stream", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }
        item {
            OutlinedTextField(value = streamTitle, onValueChange = { streamTitle = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Stream title") })
        }
        item {
            Button(onClick = { viewModel.createStream(streamTitle, "1080p", "30fps") },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.streamState !is UiState.Loading) { Text("Create Stream (1080p / 30fps)") }
        }
        when (val s = state.streamState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            is UiState.Data -> {
                item { Text("Stream ID: ${state.currentStreamId}", color = MaterialTheme.colorScheme.primary) }
                s.value.cdn?.ingestionInfo?.let { info ->
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("RTMP Ingestion", style = MaterialTheme.typography.titleSmall)
                                info.ingestionAddress?.let { Text("URL: $it") }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        if (streamKeyVisible) info.streamName ?: "—"
                                        else "••••••••••••••••",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                    OutlinedButton(onClick = { streamKeyVisible = !streamKeyVisible }) {
                                        Text(if (streamKeyVisible) "Hide" else "Reveal key")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> Unit
        }

        item { Divider() }

        // Transitions
        item {
            Text("3. Broadcast State", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }
        state.currentLifeCycleStatus?.let {
            item { Text("Status: $it", style = MaterialTheme.typography.bodyLarge) }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.bindAndTransition("testing") },
                    enabled = state.transitionState !is UiState.Loading) { Text("Testing") }
                Button(onClick = { viewModel.bindAndTransition("live") },
                    enabled = state.transitionState !is UiState.Loading) { Text("Go Live") }
                Button(onClick = { viewModel.bindAndTransition("complete") },
                    enabled = state.transitionState !is UiState.Loading) { Text("End") }
            }
        }
        when (val s = state.transitionState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            else -> Unit
        }
    }
}
