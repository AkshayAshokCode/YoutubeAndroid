package examples.api_key_only.getchannelinfo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Query

interface GetChannelInfoApi {
    @GET("youtube/v3/channels")
    suspend fun channels(
        @Query("part") part: String = "snippet,contentDetails,statistics,brandingSettings",
        @Query("id") ids: String,
        @Query("key") apiKey: String,
    ): GetChannelInfoResponse
}

class GetChannelInfo(
    private val api: GetChannelInfoApi,
    private val apiKey: String,
) {
    fun execute(channelIds: List<String>): Flow<List<ChannelInfo>> = flow {
        val ids = channelIds.map(String::trim).filter(String::isNotBlank).take(50)
        require(ids.isNotEmpty()) { "At least one channel ID is required." }
        emit(api.channels(ids = ids.joinToString(","), apiKey = apiKey).items)
    }
}

data class GetChannelInfoResponse(val items: List<ChannelInfo> = emptyList())
data class ChannelInfo(val id: String? = null, val snippet: ChannelSnippet? = null, val contentDetails: ChannelContentDetails? = null, val statistics: ChannelStatistics? = null)
data class ChannelSnippet(val title: String? = null, val description: String? = null, val country: String? = null)
data class ChannelContentDetails(val relatedPlaylists: RelatedPlaylists? = null)
data class RelatedPlaylists(val uploads: String? = null, val likes: String? = null)
data class ChannelStatistics(val viewCount: String? = null, val subscriberCount: String? = null, val videoCount: String? = null, val hiddenSubscriberCount: Boolean? = null)
