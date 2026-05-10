package examples.oauth_required.livechat

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LiveChatApi {
    @GET("youtube/v3/liveChat/messages")
    suspend fun messages(@Header("Authorization") auth: String, @Query("liveChatId") liveChatId: String, @Query("part") part: String = "snippet,authorDetails", @Query("pageToken") pageToken: String? = null): LiveChatMessagesResponse

    @POST("youtube/v3/liveChat/messages")
    suspend fun post(@Header("Authorization") auth: String, @Query("part") part: String = "snippet", @Body body: LiveChatMessageRequest): LiveChatMessage
}

class LiveChat(private val api: LiveChatApi, private val accessTokenProvider: suspend () -> String) {
    fun poll(liveChatId: String): Flow<LiveChatMessagesResponse> = flow {
        var nextPageToken: String? = null
        while (true) {
            val response = api.messages("Bearer ${accessTokenProvider()}", liveChatId, pageToken = nextPageToken)
            emit(response)
            nextPageToken = response.nextPageToken
            delay(response.pollingIntervalMillis ?: 5_000L)
        }
    }
}

data class LiveChatMessagesResponse(val nextPageToken: String? = null, val pollingIntervalMillis: Long? = null, val items: List<LiveChatMessage> = emptyList())
data class LiveChatMessage(val id: String? = null, val snippet: MessageSnippet? = null)
data class LiveChatMessageRequest(val snippet: MessageSnippet)
data class MessageSnippet(val liveChatId: String, val type: String = "textMessageEvent", val textMessageDetails: TextMessageDetails)
data class TextMessageDetails(val messageText: String)
