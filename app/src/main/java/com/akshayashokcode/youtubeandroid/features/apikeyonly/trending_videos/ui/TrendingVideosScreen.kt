package com.akshayashokcode.youtubeandroid.features.apikeyonly.trending_videos.ui

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
import com.akshayashokcode.youtubeandroid.features.apikeyonly.trending_videos.TrendingVideosViewModel

@Composable
fun TrendingVideosScreen(
    onBackClick: () -> Unit,
    viewModel: TrendingVideosViewModel = hiltViewModel(),
) {
    var regionCode by rememberSaveable { mutableStateOf("US") }
    var categoryId by rememberSaveable { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { OutlinedButton(onClick = onBackClick) { Text("Back") } }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Trending Videos", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoint: videos.list chart=mostPopular · Quota: 1 unit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Text("Explore region and category-based popular public videos.",
                style = MaterialTheme.typography.bodyLarge)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = regionCode,
                    onValueChange = { regionCode = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Region code") },
                    placeholder = { Text("US") },
                )
                OutlinedTextField(
                    value = categoryId,
                    onValueChange = { categoryId = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Category ID (opt.)") },
                    placeholder = { Text("10") },
                )
            }
        }

        item {
            Button(
                onClick = { viewModel.load(regionCode, categoryId.ifBlank { null }) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is UiState.Loading,
            ) { Text("Load Trending") }
        }

        when (val state = uiState) {
            UiState.Idle -> Unit
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is UiState.Data -> {
                val videos = state.value.items
                if (videos.isEmpty()) {
                    item { Text("No trending videos found.") }
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
                                    Text(it, style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold)
                                }
                                video.snippet?.channelTitle?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    video.statistics?.viewCount?.let {
                                        Text("Views: $it",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    video.contentDetails?.duration?.let {
                                        Text("Duration: $it",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                    state.value.nextPageToken?.let { token ->
                        item {
                            OutlinedButton(
                                onClick = { viewModel.load(regionCode, categoryId.ifBlank { null }, token) },
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text("Load next page") }
                        }
                    }
                }
            }
        }
    }
}
