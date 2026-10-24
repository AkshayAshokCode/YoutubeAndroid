package com.akshayashokcode.youtubeandroid.features.apikeyonly.categories_regions.ui

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
import androidx.compose.material3.Surface
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
import com.akshayashokcode.youtubeandroid.features.apikeyonly.categories_regions.CategoriesRegionsViewModel

@Composable
fun CategoriesRegionsScreen(
    onBackClick: () -> Unit,
    viewModel: CategoriesRegionsViewModel = hiltViewModel(),
) {
    var regionCode by rememberSaveable { mutableStateOf("US") }
    var language by rememberSaveable { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { OutlinedButton(onClick = onBackClick) { Text("Back") } }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Categories & Regions", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoints: videoCategories.list · i18nRegions.list · Quota: 1 unit/request",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
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
                    value = language,
                    onValueChange = { language = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Language (opt.)") },
                    placeholder = { Text("en") },
                )
            }
        }

        item {
            Button(
                onClick = { viewModel.load(regionCode, language.ifBlank { null }) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Load") }
        }

        // Categories section
        item {
            Text("Video Categories", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }

        when (val cs = state.categories) {
            UiState.Idle -> item { Text("Enter a region and tap Load.",
                color = MaterialTheme.colorScheme.onSurfaceVariant) }
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(cs.message, color = MaterialTheme.colorScheme.error) }
            is UiState.Data -> {
                val cats = cs.value.items
                if (cats.isEmpty()) {
                    item { Text("No categories returned.") }
                } else {
                    items(cats) { cat ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(cat.snippet?.title ?: cat.id ?: "—",
                                style = MaterialTheme.typography.bodyMedium)
                            if (cat.snippet?.assignable == true) {
                                AssignableBadge()
                            }
                        }
                    }
                }
            }
        }

        // Regions section
        item {
            Text("Supported Regions", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }

        when (val rs = state.regions) {
            UiState.Idle -> Unit
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(rs.message, color = MaterialTheme.colorScheme.error) }
            is UiState.Data -> {
                val regions = rs.value.items
                if (regions.isEmpty()) {
                    item { Text("No regions returned.") }
                } else {
                    items(regions) { region ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(region.snippet?.name ?: "—",
                                    style = MaterialTheme.typography.bodyMedium)
                                Text(region.snippet?.gl ?: region.id ?: "—",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AssignableBadge() {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            "Assignable",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
