package examples.oauth_required.livestreaming

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LiveStreamingApi {
    @POST("youtube/v3/liveBroadcasts")
    suspend fun createBroadcast(@Header("Authorization") auth: String, @Query("part") part: String = "snippet,status,contentDetails", @Body body: CreateBroadcastRequest): LiveBroadcastResponse

    @POST("youtube/v3/liveStreams")
    suspend fun createStream(@Header("Authorization") auth: String, @Query("part") part: String = "snippet,cdn", @Body body: CreateStreamRequest): LiveStreamResponse

    @POST("youtube/v3/liveBroadcasts/bind")
    suspend fun bind(@Header("Authorization") auth: String, @Query("id") broadcastId: String, @Query("streamId") streamId: String, @Query("part") part: String = "id,contentDetails"): LiveBroadcastResponse

    @POST("youtube/v3/liveBroadcasts/transition")
    suspend fun transition(@Header("Authorization") auth: String, @Query("id") broadcastId: String, @Query("broadcastStatus") status: String, @Query("part") part: String = "id,status"): LiveBroadcastResponse
}

class LiveStreaming(private val api: LiveStreamingApi, private val accessTokenProvider: suspend () -> String) {
    fun createUnlistedEvent(title: String, scheduledStartTime: String): Flow<LiveSetup> = flow {
        val auth = "Bearer ${accessTokenProvider()}"
        val broadcast = api.createBroadcast(auth, body = CreateBroadcastRequest(LiveSnippet(title, scheduledStartTime), LiveStatus("unlisted")))
        val stream = api.createStream(auth, body = CreateStreamRequest(StreamSnippet("Android RTMP stream"), CdnSettings()))
        api.bind(auth, broadcastId = broadcast.id, streamId = stream.id)
        emit(LiveSetup(broadcast.id, stream.id, stream.cdn.ingestionInfo.ingestionAddress, stream.cdn.ingestionInfo.streamName))
    }
}

data class CreateBroadcastRequest(val snippet: LiveSnippet, val status: LiveStatus)
data class LiveSnippet(val title: String, val scheduledStartTime: String)
data class LiveStatus(val privacyStatus: String)
data class CreateStreamRequest(val snippet: StreamSnippet, val cdn: CdnSettings)
data class StreamSnippet(val title: String)
data class CdnSettings(val format: String = "1080p", val ingestionType: String = "rtmp", val resolution: String = "1080p", val frameRate: String = "30fps")
data class LiveBroadcastResponse(val id: String)
data class LiveStreamResponse(val id: String, val cdn: CdnResponse)
data class CdnResponse(val ingestionInfo: IngestionInfo)
data class IngestionInfo(val ingestionAddress: String, val streamName: String)
data class LiveSetup(val broadcastId: String, val streamId: String, val rtmpUrl: String, val streamKey: String)
