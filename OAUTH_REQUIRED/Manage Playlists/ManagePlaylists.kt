package examples.oauth_required.manageplaylists

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ManagePlaylistsApi {
    @POST("youtube/v3/playlists")
    suspend fun createPlaylist(@Header("Authorization") auth: String, @Query("part") part: String = "snippet,status", @Body body: PlaylistRequest): PlaylistResponse

    @POST("youtube/v3/playlistItems")
    suspend fun addVideo(@Header("Authorization") auth: String, @Query("part") part: String = "snippet", @Body body: PlaylistItemRequest): PlaylistItemResponse

    @DELETE("youtube/v3/playlistItems")
    suspend fun removeVideo(@Header("Authorization") auth: String, @Query("id") playlistItemId: String)
}

class ManagePlaylists(private val api: ManagePlaylistsApi, private val accessTokenProvider: suspend () -> String) {
    fun create(title: String, privacyStatus: String = "private"): Flow<PlaylistResponse> = flow {
        val auth = "Bearer ${accessTokenProvider()}"
        emit(api.createPlaylist(auth, body = PlaylistRequest(PlaylistSnippet(title), PlaylistStatus(privacyStatus))))
    }
}

data class PlaylistRequest(val snippet: PlaylistSnippet, val status: PlaylistStatus)
data class PlaylistSnippet(val title: String, val description: String? = null)
data class PlaylistStatus(val privacyStatus: String)
data class PlaylistResponse(val id: String)
data class PlaylistItemRequest(val snippet: PlaylistItemSnippet)
data class PlaylistItemSnippet(val playlistId: String, val resourceId: ResourceId)
data class ResourceId(val kind: String = "youtube#video", val videoId: String)
data class PlaylistItemResponse(val id: String)
