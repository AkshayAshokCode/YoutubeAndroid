package examples.api_key_only.searchvideos

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchVideosApi {
    @GET("youtube/v3/search")
    suspend fun search(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 25,
        @Query("pageToken") pageToken: String? = null,
        @Query("key") apiKey: String,
    ): SearchVideosResponse
}

class SearchVideos(
    private val api: SearchVideosApi,
    private val apiKey: String,
) {
    fun execute(
        query: String,
        type: SearchResultType = SearchResultType.Video,
        pageToken: String? = null,
    ): Flow<SearchVideosResult> = flow {
        require(query.isNotBlank()) { "Search query cannot be blank." }
        val response = api.search(
            query = query.trim(),
            type = type.apiValue,
            pageToken = pageToken,
            apiKey = apiKey,
        )
        emit(SearchVideosResult.Success(response.items, response.nextPageToken))
    }
}

enum class SearchResultType(val apiValue: String) {
    Video("video"),
    Channel("channel"),
    Playlist("playlist"),
}

sealed interface SearchVideosResult {
    data class Success(
        val items: List<SearchItem>,
        val nextPageToken: String?,
    ) : SearchVideosResult
}

data class SearchVideosResponse(
    val nextPageToken: String? = null,
    val items: List<SearchItem> = emptyList(),
)

data class SearchItem(
    val id: SearchId? = null,
    val snippet: SearchSnippet? = null,
)

data class SearchId(
    val videoId: String? = null,
    val channelId: String? = null,
    val playlistId: String? = null,
)

data class SearchSnippet(
    val title: String? = null,
    val description: String? = null,
    val channelTitle: String? = null,
    val thumbnails: Map<String, Thumbnail>? = null,
)

data class Thumbnail(val url: String? = null)
