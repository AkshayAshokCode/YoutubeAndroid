package examples.api_key_only.browseplaylists

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Query

interface BrowsePlaylistsApi {
    @GET("youtube/v3/playlists")
    suspend fun playlists(@Query("part") part: String = "snippet,contentDetails", @Query("channelId") channelId: String, @Query("pageToken") pageToken: String? = null, @Query("key") apiKey: String): PlaylistResponse

    @GET("youtube/v3/playlistItems")
    suspend fun playlistItems(@Query("part") part: String = "snippet,contentDetails", @Query("playlistId") playlistId: String, @Query("pageToken") pageToken: String? = null, @Query("key") apiKey: String): PlaylistItemsResponse
}

class BrowsePlaylists(private val api: BrowsePlaylistsApi, private val apiKey: String) {
    fun channelPlaylists(channelId: String, pageToken: String? = null): Flow<PlaylistResponse> = flow {
        require(channelId.isNotBlank()) { "Channel ID is required." }
        emit(api.playlists(channelId = channelId.trim(), pageToken = pageToken, apiKey = apiKey))
    }

    fun playlistVideos(playlistId: String, pageToken: String? = null): Flow<PlaylistItemsResponse> = flow {
        require(playlistId.isNotBlank()) { "Playlist ID is required." }
        emit(api.playlistItems(playlistId = playlistId.trim(), pageToken = pageToken, apiKey = apiKey))
    }
}

data class PlaylistResponse(val nextPageToken: String? = null, val items: List<Playlist> = emptyList())
data class PlaylistItemsResponse(val nextPageToken: String? = null, val items: List<PlaylistVideo> = emptyList())
data class Playlist(val id: String? = null, val snippet: PlaylistSnippet? = null, val contentDetails: PlaylistContentDetails? = null)
data class PlaylistSnippet(val title: String? = null, val description: String? = null)
data class PlaylistContentDetails(val itemCount: Int? = null)
data class PlaylistVideo(val id: String? = null, val snippet: PlaylistVideoSnippet? = null)
data class PlaylistVideoSnippet(val title: String? = null, val position: Int? = null, val resourceId: ResourceId? = null)
data class ResourceId(val videoId: String? = null)
