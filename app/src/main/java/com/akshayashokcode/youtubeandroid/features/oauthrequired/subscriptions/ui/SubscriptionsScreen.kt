package com.akshayashokcode.youtubeandroid.features.oauthrequired.subscriptions.ui

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
import com.akshayashokcode.youtubeandroid.features.oauthrequired.subscriptions.SubscriptionsViewModel

@Composable
fun SubscriptionsScreen(
    onBackClick: () -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel(),
) {
    var channelId by rememberSaveable { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { OutlinedButton(onClick = onBackClick) { Text("Back") } }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Subscribe to Channels", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoints: subscriptions.insert · subscriptions.delete\nScope: youtube · Quota: 50 units/write",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Text("Subscribe or unsubscribe the authenticated account from a channel.",
                style = MaterialTheme.typography.bodyLarge)
        }

        item {
            OutlinedTextField(
                value = channelId,
                onValueChange = { channelId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Channel ID") },
                placeholder = { Text("UCxxxxxxxxxxxxxxxxxxxxxx") },
            )
        }

        item {
            Button(
                onClick = { viewModel.subscribe(channelId) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.subscribeState !is UiState.Loading && state.subscriptionId == null,
            ) { Text("Subscribe") }
        }

        when (val s = state.subscribeState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            is UiState.Data -> item {
                Text("Subscribed! ID: ${state.subscriptionId}",
                    color = MaterialTheme.colorScheme.primary)
            }
            else -> Unit
        }

        state.subscriptionId?.let { subId ->
            item {
                Button(
                    onClick = { viewModel.unsubscribe(subId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    enabled = state.unsubscribeState !is UiState.Loading,
                ) { Text("Unsubscribe") }
            }
        }

        when (val s = state.unsubscribeState) {
            is UiState.Data -> item { Text("Unsubscribed.", color = MaterialTheme.colorScheme.primary) }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            else -> Unit
        }
    }
}
