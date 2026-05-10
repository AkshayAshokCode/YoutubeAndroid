package com.akshayashokcode.youtubeandroid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akshayashokcode.youtubeandroid.BuildConfig

private enum class AppPath {
    Root,
    ApiKeyApis,
    OAuthApis,
}

private data class ApiPath(
    val title: String,
    val subtitle: String,
    val purpose: String,
    val badge: String,
    val status: String,
    val features: List<FeatureItem>,
)

private data class FeatureItem(
    val name: String,
    val endpoint: String,
    val quota: String,
    val description: String,
    val status: String,
)

private val apiKeyFeatures = listOf(
    FeatureItem(
        name = "Search",
        endpoint = "search.list",
        quota = "100 units",
        description = "Find public videos, channels, and playlists by query.",
        status = "Runnable",
    ),
    FeatureItem(
        name = "Video Details",
        endpoint = "videos.list",
        quota = "1 unit",
        description = "Read metadata, duration, statistics, status, and live details.",
        status = "Runnable",
    ),
    FeatureItem(
        name = "Channel Details",
        endpoint = "channels.list",
        quota = "1 unit",
        description = "Read public channel metadata, uploads playlist, and statistics.",
        status = "Runnable",
    ),
    FeatureItem(
        name = "Public Playlists",
        endpoint = "playlists.list + playlistItems.list",
        quota = "1 unit per request",
        description = "Browse public channel playlists and playlist videos.",
        status = "Runnable",
    ),
    FeatureItem(
        name = "Trending Videos",
        endpoint = "videos.list chart=mostPopular",
        quota = "1 unit",
        description = "Explore region/category-based popular public videos.",
        status = "Planned",
    ),
    FeatureItem(
        name = "Categories & Regions",
        endpoint = "videoCategories.list + i18nRegions.list",
        quota = "1 unit per request",
        description = "Load assignable video categories and supported YouTube regions.",
        status = "Runnable",
    ),
)

private val oauthFeatures = listOf(
    FeatureItem(
        name = "Upload Videos",
        endpoint = "videos.insert",
        quota = "1600 units",
        description = "Upload videos with metadata, privacy, tags, and made-for-kids state.",
        status = "Requires sign-in",
    ),
    FeatureItem(
        name = "Comments",
        endpoint = "commentThreads.insert + comments.delete",
        quota = "50 units per write",
        description = "Post comments and delete comments owned by the signed-in user.",
        status = "Requires sign-in",
    ),
    FeatureItem(
        name = "Playlist Management",
        endpoint = "playlists.insert + playlistItems.*",
        quota = "50 units per write",
        description = "Create playlists and add or remove videos from account playlists.",
        status = "Requires sign-in",
    ),
    FeatureItem(
        name = "Subscriptions",
        endpoint = "subscriptions.insert + subscriptions.delete",
        quota = "50 units per write",
        description = "Subscribe and unsubscribe the authenticated account from channels.",
        status = "Requires sign-in",
    ),
    FeatureItem(
        name = "Live Streaming",
        endpoint = "liveBroadcasts.* + liveStreams.*",
        quota = "50+ units",
        description = "Create broadcasts, bind streams, and transition live states.",
        status = "Requires sign-in",
    ),
    FeatureItem(
        name = "Live Chat",
        endpoint = "liveChatMessages.list + liveChatMessages.insert",
        quota = "1+ units",
        description = "Read and post messages for an active live chat.",
        status = "Requires sign-in",
    ),
    FeatureItem(
        name = "Captions",
        endpoint = "captions.*",
        quota = "50+ units",
        description = "List, upload, update, and delete caption tracks.",
        status = "Requires sign-in",
    ),
)

private val apiKeyPath = ApiPath(
    title = "API Key APIs",
    subtitle = "Public YouTube data without user login",
    purpose = "Explore public YouTube Data API endpoints using only an API key. These features do not require Google Sign-In.",
    badge = "Beginner friendly",
    status = if (BuildConfig.YOUTUBE_API_KEY.isBlank()) "API key missing" else "API key configured",
    features = apiKeyFeatures,
)

private val oauthPath = ApiPath(
    title = "OAuth APIs (Advanced)",
    subtitle = "Account-scoped APIs requiring Google Sign-In",
    purpose = "Explore advanced YouTube APIs that read or modify a signed-in YouTube account with OAuth 2.0 scopes.",
    badge = "Advanced",
    status = "Google Sign-In required",
    features = oauthFeatures,
)

@Composable
fun YouTubeExamplesApp() {
    var currentPath by rememberSaveable { mutableStateOf(AppPath.Root.name) }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            when (AppPath.valueOf(currentPath)) {
                AppPath.Root -> RootSelectionScreen(
                    onApiKeyApisClick = { currentPath = AppPath.ApiKeyApis.name },
                    onOAuthApisClick = { currentPath = AppPath.OAuthApis.name },
                )

                AppPath.ApiKeyApis -> FeatureListScreen(
                    apiPath = apiKeyPath,
                    onBackClick = { currentPath = AppPath.Root.name },
                )

                AppPath.OAuthApis -> FeatureListScreen(
                    apiPath = oauthPath,
                    onBackClick = { currentPath = AppPath.Root.name },
                )
            }
        }
    }
}

@Composable
private fun RootSelectionScreen(
    onApiKeyApisClick: () -> Unit,
    onOAuthApisClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Header(
                title = "YouTube API Examples",
                subtitle = "Choose the correct auth path before exploring a YouTube Data API feature.",
            )
        }
        item {
            Text(
                text = "Public APIs use an API key. Account-scoped APIs require Google Sign-In and OAuth consent.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            ApiPathCard(
                apiPath = apiKeyPath,
                actionLabel = "Explore API Key APIs",
                onClick = onApiKeyApisClick,
            )
        }
        item {
            ApiPathCard(
                apiPath = oauthPath,
                actionLabel = "Explore OAuth APIs",
                onClick = onOAuthApisClick,
            )
        }
    }
}

@Composable
private fun FeatureListScreen(
    apiPath: ApiPath,
    onBackClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            OutlinedButton(onClick = onBackClick) {
                Text(text = "Back to root selection")
            }
        }
        item {
            Header(
                title = apiPath.title,
                subtitle = apiPath.subtitle,
            )
        }
        item {
            Text(
                text = apiPath.purpose,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LabelPill(text = apiPath.badge)
                LabelPill(text = apiPath.status)
            }
        }
        items(apiPath.features) { feature ->
            FeatureCard(feature = feature)
        }
    }
}

@Composable
private fun ApiPathCard(
    apiPath: ApiPath,
    actionLabel: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = apiPath.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = apiPath.subtitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                LabelPill(text = apiPath.badge)
            }
            Text(
                text = apiPath.purpose,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Status: ${apiPath.status}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Includes: ${apiPath.features.joinToString { it.name }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onClick) {
                Text(text = actionLabel)
            }
        }
    }
}

@Composable
private fun FeatureCard(feature: FeatureItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = feature.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(12.dp))
                LabelPill(text = feature.status)
            }
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Endpoint: ${feature.endpoint}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Quota: ${feature.quota}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Header(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LabelPill(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
