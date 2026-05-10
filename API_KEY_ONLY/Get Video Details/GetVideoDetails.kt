package examples.api_key_only.getvideodetails

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Query

interface GetVideoDetailsApi {
    @GET("youtube/v3/videos")
    suspend fun videos(
        @Query("part") part: String = "snippet,contentDetails,statistics,status,liveStreamingDetails",
        @Query("id") ids: String,
        @Query("key") apiKey: String,
    ): GetVideoDetailsResponse
}

class GetVideoDetails(
    private val api: GetVideoDetailsApi,
    private val apiKey: String,
) {
    fun execute(videoIds: List<String>): Flow<List<VideoDetails>> = flow {
        val ids = videoIds.map(String::trim).filter(String::isNotBlank).take(50)
        require(ids.isNotEmpty()) { "At least one video ID is required." }
        emit(api.videos(ids = ids.joinToString(","), apiKey = apiKey).items)
    }
}

data class GetVideoDetailsResponse(val items: List<VideoDetails> = emptyList())

data class VideoDetails(
    val id: String? = null,
    val snippet: VideoSnippet? = null,
    val contentDetails: ContentDetails? = null,
    val statistics: VideoStatistics? = null,
    val status: VideoStatus? = null,
    val liveStreamingDetails: LiveStreamingDetails? = null,
)

data class VideoSnippet(val title: String? = null, val description: String? = null, val channelTitle: String? = null)
data class ContentDetails(val duration: String? = null, val definition: String? = null, val caption: String? = null)
data class VideoStatistics(val viewCount: String? = null, val likeCount: String? = null, val commentCount: String? = null)
data class VideoStatus(val privacyStatus: String? = null, val embeddable: Boolean? = null)
data class LiveStreamingDetails(val scheduledStartTime: String? = null, val activeLiveChatId: String? = null)
