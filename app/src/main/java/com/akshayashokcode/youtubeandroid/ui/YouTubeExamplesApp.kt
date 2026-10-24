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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.akshayashokcode.youtubeandroid.BuildConfig
import com.akshayashokcode.youtubeandroid.core.auth.AuthState
import com.akshayashokcode.youtubeandroid.core.auth.AuthViewModel
import com.akshayashokcode.youtubeandroid.features.apikeyonly.categories_regions.ui.CategoriesRegionsScreen
import com.akshayashokcode.youtubeandroid.features.apikeyonly.channel_info.ui.ChannelInfoScreen
import com.akshayashokcode.youtubeandroid.features.apikeyonly.public_playlists.ui.BrowsePlaylistsScreen
import com.akshayashokcode.youtubeandroid.features.apikeyonly.search.ui.SearchScreen
import com.akshayashokcode.youtubeandroid.features.apikeyonly.trending_videos.ui.TrendingVideosScreen
import com.akshayashokcode.youtubeandroid.features.apikeyonly.video_details.ui.VideoDetailsScreen
import com.akshayashokcode.youtubeandroid.features.oauthrequired.captions.ui.ManageCaptionsScreen
import com.akshayashokcode.youtubeandroid.features.oauthrequired.comments.ui.PostCommentsScreen
import com.akshayashokcode.youtubeandroid.features.oauthrequired.live_chat.ui.LiveChatScreen
import com.akshayashokcode.youtubeandroid.features.oauthrequired.live_streaming.ui.LiveStreamingScreen
import com.akshayashokcode.youtubeandroid.features.oauthrequired.manage_playlists.ui.ManagePlaylistsScreen
import com.akshayashokcode.youtubeandroid.features.oauthrequired.subscriptions.ui.SubscriptionsScreen
import com.akshayashokcode.youtubeandroid.features.oauthrequired.upload_video.ui.UploadVideoScreen

// ── Route constants ───────────────────────────────────────────────────────────

private object Route {
    const val ROOT = "root"
    const val API_KEY_APIS = "apiKeyApis"
    const val OAUTH_APIS = "oauthApis"
    const val SEARCH = "search"
    const val VIDEO_DETAILS = "videoDetails"
    const val CHANNEL_INFO = "channelInfo"
    const val BROWSE_PLAYLISTS = "browsePlaylists?playlistId={playlistId}"
    const val CATEGORIES_REGIONS = "categoriesRegions"
    const val TRENDING_VIDEOS = "trendingVideos"
    const val POST_COMMENTS = "postComments"
    const val MANAGE_PLAYLISTS = "managePlaylists"
    const val SUBSCRIPTIONS = "subscriptions"
    const val MANAGE_CAPTIONS = "manageCaptions"
    const val UPLOAD_VIDEO = "uploadVideo"
    const val LIVE_STREAMING = "liveStreaming"
    const val LIVE_CHAT = "liveChat"

    fun browsePlaylists(playlistId: String? = null) =
        "browsePlaylists?playlistId=${playlistId ?: ""}"
}

// ── Feature metadata ──────────────────────────────────────────────────────────

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
    val route: String,
)

private val apiKeyFeatures = listOf(
    FeatureItem("Search", "search.list", "100 units",
        "Find public videos, channels, and playlists by query.", "Runnable", Route.SEARCH),
    FeatureItem("Video Details", "videos.list", "1 unit",
        "Read metadata, duration, statistics, status, and live details.", "Runnable", Route.VIDEO_DETAILS),
    FeatureItem("Channel Info", "channels.list", "1 unit",
        "Read public channel metadata, uploads playlist, and statistics.", "Runnable", Route.CHANNEL_INFO),
    FeatureItem("Browse Playlists", "playlists.list + playlistItems.list", "1 unit/request",
        "Browse public channel playlists and playlist videos.", "Runnable", Route.browsePlaylists()),
    FeatureItem("Trending Videos", "videos.list chart=mostPopular", "1 unit",
        "Explore region/category-based popular public videos.", "Runnable", Route.TRENDING_VIDEOS),
    FeatureItem("Categories & Regions", "videoCategories.list + i18nRegions.list", "1 unit/request",
        "Load assignable video categories and supported YouTube regions.", "Runnable", Route.CATEGORIES_REGIONS),
)

private val oauthFeatures = listOf(
    FeatureItem("Upload Videos", "videos.insert", "1600 units",
        "Upload videos with metadata, privacy, tags, and made-for-kids state.", "Requires sign-in", Route.UPLOAD_VIDEO),
    FeatureItem("Comments", "commentThreads.insert + comments.delete", "50 units/write",
        "Post comments and delete comments owned by the signed-in user.", "Requires sign-in", Route.POST_COMMENTS),
    FeatureItem("Manage Playlists", "playlists.insert + playlistItems.*", "50 units/write",
        "Create playlists and add or remove videos from account playlists.", "Requires sign-in", Route.MANAGE_PLAYLISTS),
    FeatureItem("Subscriptions", "subscriptions.insert + subscriptions.delete", "50 units/write",
        "Subscribe and unsubscribe the authenticated account from channels.", "Requires sign-in", Route.SUBSCRIPTIONS),
    FeatureItem("Live Streaming", "liveBroadcasts.* + liveStreams.*", "50+ units",
        "Create broadcasts, bind streams, and transition live states.", "Requires sign-in", Route.LIVE_STREAMING),
    FeatureItem("Live Chat", "liveChatMessages.list + liveChatMessages.insert", "1+ units",
        "Read and post messages for an active live chat.", "Requires sign-in", Route.LIVE_CHAT),
    FeatureItem("Captions", "captions.*", "50+ units",
        "List, upload, update, and delete caption tracks.", "Requires sign-in", Route.MANAGE_CAPTIONS),
)

// ── App root ──────────────────────────────────────────────────────────────────

@Composable
fun YouTubeExamplesApp(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NavHost(navController = navController, startDestination = Route.ROOT) {

                composable(Route.ROOT) {
                    RootSelectionScreen(
                        authState = authState,
                        onSignIn = { authViewModel.signIn() },
                        onSignOut = { authViewModel.signOut() },
                        onApiKeyApisClick = { navController.navigate(Route.API_KEY_APIS) },
                        onOAuthApisClick = { navController.navigate(Route.OAUTH_APIS) },
                    )
                }

                composable(Route.API_KEY_APIS) {
                    FeatureListScreen(
                        apiPath = ApiPath(
                            title = "API Key APIs",
                            subtitle = "Public YouTube data without user login",
                            purpose = "Explore public YouTube Data API endpoints using only an API key.",
                            badge = "Beginner friendly",
                            status = if (BuildConfig.YOUTUBE_API_KEY.isBlank()) "API key missing"
                            else "API key configured",
                            features = apiKeyFeatures,
                        ),
                        isOAuth = false,
                        authState = authState,
                        onBackClick = { navController.popBackStack() },
                        onFeatureClick = { navController.navigate(it.route) },
                    )
                }

                composable(Route.OAUTH_APIS) {
                    FeatureListScreen(
                        apiPath = ApiPath(
                            title = "OAuth APIs (Advanced)",
                            subtitle = "Account-scoped APIs requiring Google Sign-In",
                            purpose = "Explore advanced YouTube APIs that read or modify a signed-in YouTube account.",
                            badge = "Advanced",
                            status = when (authState) {
                                is AuthState.SignedIn -> "Signed in as ${(authState as AuthState.SignedIn).email}"
                                else -> "Sign-in required"
                            },
                            features = oauthFeatures,
                        ),
                        isOAuth = true,
                        authState = authState,
                        onBackClick = { navController.popBackStack() },
                        onFeatureClick = { feature ->
                            if (authState is AuthState.SignedIn) {
                                navController.navigate(feature.route)
                            }
                        },
                    )
                }

                composable(Route.SEARCH) {
                    SearchScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Route.VIDEO_DETAILS) {
                    VideoDetailsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Route.CHANNEL_INFO) {
                    ChannelInfoScreen(
                        onBackClick = { navController.popBackStack() },
                        onBrowsePlaylistsClick = { uploadsId ->
                            navController.navigate(Route.browsePlaylists(uploadsId))
                        },
                    )
                }

                composable(
                    route = Route.BROWSE_PLAYLISTS,
                    arguments = listOf(navArgument("playlistId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }),
                ) { back ->
                    val playlistId = back.arguments?.getString("playlistId")?.ifBlank { null }
                    BrowsePlaylistsScreen(
                        initialPlaylistId = playlistId,
                        onBackClick = { navController.popBackStack() },
                    )
                }

                composable(Route.CATEGORIES_REGIONS) {
                    CategoriesRegionsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Route.TRENDING_VIDEOS) {
                    TrendingVideosScreen(onBackClick = { navController.popBackStack() })
                }

                // OAuth screens
                composable(Route.POST_COMMENTS) {
                    PostCommentsScreen(onBackClick = { navController.popBackStack() })
                }
                composable(Route.MANAGE_PLAYLISTS) {
                    ManagePlaylistsScreen(onBackClick = { navController.popBackStack() })
                }
                composable(Route.SUBSCRIPTIONS) {
                    SubscriptionsScreen(onBackClick = { navController.popBackStack() })
                }
                composable(Route.MANAGE_CAPTIONS) {
                    ManageCaptionsScreen(onBackClick = { navController.popBackStack() })
                }
                composable(Route.UPLOAD_VIDEO) {
                    UploadVideoScreen(onBackClick = { navController.popBackStack() })
                }
                composable(Route.LIVE_STREAMING) {
                    LiveStreamingScreen(onBackClick = { navController.popBackStack() })
                }
                composable(Route.LIVE_CHAT) {
                    LiveChatScreen(onBackClick = { navController.popBackStack() })
                }
            }
        }
    }
}

// ── Screens ───────────────────────────────────────────────────────────────────

@Composable
private fun RootSelectionScreen(
    authState: AuthState,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onApiKeyApisClick: () -> Unit,
    onOAuthApisClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
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
                "Public APIs use an API key. Account-scoped APIs require Google Sign-In and OAuth consent.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { AuthCard(authState = authState, onSignIn = onSignIn, onSignOut = onSignOut) }
        item {
            ApiPathCard(
                title = "API Key APIs",
                subtitle = "Public YouTube data without user login",
                badge = "Beginner friendly",
                status = if (BuildConfig.YOUTUBE_API_KEY.isBlank()) "API key missing" else "API key configured",
                includes = apiKeyFeatures.joinToString { it.name },
                actionLabel = "Explore API Key APIs",
                onClick = onApiKeyApisClick,
            )
        }
        item {
            ApiPathCard(
                title = "OAuth APIs (Advanced)",
                subtitle = "Account-scoped APIs requiring Google Sign-In",
                badge = "Advanced",
                status = when (authState) {
                    is AuthState.SignedIn -> "Signed in as ${authState.email}"
                    else -> "Sign-in required"
                },
                includes = oauthFeatures.joinToString { it.name },
                actionLabel = "Explore OAuth APIs",
                onClick = onOAuthApisClick,
            )
        }
    }
}

@Composable
private fun AuthCard(authState: AuthState, onSignIn: () -> Unit, onSignOut: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Google Account", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            when (authState) {
                AuthState.SignedOut -> {
                    Text("Not signed in. OAuth features require Google Sign-In.",
                        style = MaterialTheme.typography.bodySmall)
                    Button(onClick = onSignIn) { Text("Sign in (Demo)") }
                }
                AuthState.SigningIn -> {
                    Text("Signing in…", style = MaterialTheme.typography.bodySmall)
                }
                is AuthState.SignedIn -> {
                    Text("Signed in as ${authState.displayName} (${authState.email})",
                        style = MaterialTheme.typography.bodySmall)
                    Button(
                        onClick = onSignOut,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                    ) { Text("Sign out") }
                }
            }
        }
    }
}

@Composable
private fun FeatureListScreen(
    apiPath: ApiPath,
    isOAuth: Boolean,
    authState: AuthState,
    onBackClick: () -> Unit,
    onFeatureClick: (FeatureItem) -> Unit,
) {
    val isSignedIn = authState is AuthState.SignedIn
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { OutlinedButton(onClick = onBackClick) { Text("Back to root selection") } }
        item { Header(title = apiPath.title, subtitle = apiPath.subtitle) }
        item {
            Text(apiPath.purpose, style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LabelPill(apiPath.badge)
                LabelPill(apiPath.status)
            }
        }
        if (isOAuth && !isSignedIn) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        "Sign in from the Home screen to run OAuth features.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        }
        items(apiPath.features) { feature ->
            FeatureCard(
                feature = feature,
                locked = isOAuth && !isSignedIn,
                onClick = { onFeatureClick(feature) },
            )
        }
    }
}

@Composable
private fun ApiPathCard(
    title: String,
    subtitle: String,
    badge: String,
    status: String,
    includes: String,
    actionLabel: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.width(12.dp))
                LabelPill(badge)
            }
            Text("Status: $status", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Text("Includes: $includes", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onClick) { Text(actionLabel) }
        }
    }
}

@Composable
private fun FeatureCard(feature: FeatureItem, locked: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(feature.name, modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(12.dp))
                LabelPill(if (locked) "🔒 Sign in to run" else feature.status)
            }
            Text(feature.description, style = MaterialTheme.typography.bodyMedium)
            Text("Endpoint: ${feature.endpoint}", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Quota: ${feature.quota}", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Header(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
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
