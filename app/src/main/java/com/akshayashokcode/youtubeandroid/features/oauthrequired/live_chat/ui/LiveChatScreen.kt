package com.akshayashokcode.youtubeandroid.features.oauthrequired.live_chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.akshayashokcode.youtubeandroid.features.oauthrequired.live_chat.LiveChatViewModel

@Composable
fun LiveChatScreen(
    onBackClick: () -> Unit,
    viewModel: LiveChatViewModel = hiltViewModel(),
) {
    var liveChatId by rememberSaveable { mutableStateOf("") }
    var messageText by rememberSaveable { mutableStateOf("") }
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item { OutlinedButton(onClick = onBackClick) { Text("Back") } }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Live Chat", style = MaterialTheme.typography.headlineMedium)
                    Text("Endpoints: liveChatMessages.list · liveChatMessages.insert\nScope: youtube · Quota: 1+ units",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = liveChatId,
                        onValueChange = { liveChatId = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Live chat ID") },
                        placeholder = { Text("From liveBroadcast.snippet.liveChatId") },
                        singleLine = true,
                    )
                    if (!state.isPolling) {
                        OutlinedButton(onClick = { viewModel.startPolling(liveChatId) }) {
                            Text("Join")
                        }
                    } else {
                        OutlinedButton(onClick = { viewModel.stopPolling() }) {
                            Text("Leave")
                        }
                    }
                }
            }

            if (state.isPolling) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                        Text("Polling every ${state.pollingIntervalMs / 1000}s",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            when (val s = state.fetchState) {
                is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
                else -> Unit
            }

            if (state.messages.isEmpty() && state.isPolling) {
                item { Text("No messages yet…", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }

            items(state.messages) { msg ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            msg.authorDetails?.displayName ?: "Unknown",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            msg.snippet?.displayMessage
                                ?: msg.snippet?.textMessageDetails?.messageText
                                ?: "—",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }

        // Send bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Send a message…") },
                singleLine = true,
            )
            IconButton(
                onClick = {
                    viewModel.sendMessage(liveChatId, messageText)
                    messageText = ""
                },
                enabled = messageText.isNotBlank() && liveChatId.isNotBlank() &&
                        state.sendState !is UiState.Loading,
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}
