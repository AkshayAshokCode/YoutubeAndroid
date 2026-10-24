package com.akshayashokcode.youtubeandroid.features.apikeyonly.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.akshayashokcode.youtubeandroid.features.apikeyonly.search.SearchType
import com.akshayashokcode.youtubeandroid.features.apikeyonly.search.SearchViewModel

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {

    var query by rememberSaveable { mutableStateOf("") }

    var selectedType by rememberSaveable {
        mutableStateOf(SearchType.Video)
    }

    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        item {

            OutlinedButton(onClick = onBackClick) {
                Text("Back")
            }
        }

        item {

            Text(
                text = "Search Playground",
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        item {

            Text(
                text = "Explore search.list endpoint using public YouTube APIs.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        item {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Search Query")
                },
                placeholder = {
                    Text("android development")
                },
            )
        }

        item {

            Column {

                SearchType.entries.forEach { type ->

                    androidx.compose.foundation.layout.Row {

                        RadioButton(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                            },
                        )

                        Text(
                            text = type.name,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                }
            }
        }

        item {

            Button(
                onClick = {
                    viewModel.search(
                        query = query,
                        type = selectedType,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Execute Search")
            }
        }

        when (val state = uiState) {

            UiState.Idle -> Unit

            UiState.Loading -> {

                item {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {

                item {

                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            is UiState.Data -> {

                val items = state.value.items

                items(items) { item ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {

                            item.snippet?.title?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }

                            item.snippet?.channelTitle?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }

                            item.id?.kind?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}