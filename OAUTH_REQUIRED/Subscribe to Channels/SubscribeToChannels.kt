package examples.oauth_required.subscribetochannels

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SubscribeToChannelsApi {
    @POST("youtube/v3/subscriptions")
    suspend fun subscribe(@Header("Authorization") auth: String, @Query("part") part: String = "snippet", @Body body: SubscriptionRequest): SubscriptionResponse

    @DELETE("youtube/v3/subscriptions")
    suspend fun unsubscribe(@Header("Authorization") auth: String, @Query("id") subscriptionId: String)
}

class SubscribeToChannels(private val api: SubscribeToChannelsApi, private val accessTokenProvider: suspend () -> String) {
    fun subscribeTo(channelId: String): Flow<SubscriptionResponse> = flow {
        emit(api.subscribe("Bearer ${accessTokenProvider()}", body = SubscriptionRequest(SubscriptionSnippet(ResourceId(channelId = channelId)))))
    }
}

data class SubscriptionRequest(val snippet: SubscriptionSnippet)
data class SubscriptionSnippet(val resourceId: ResourceId)
data class ResourceId(val kind: String = "youtube#channel", val channelId: String)
data class SubscriptionResponse(val id: String)
