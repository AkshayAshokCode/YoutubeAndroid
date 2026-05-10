package examples.oauth_required.managecaptions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ManageCaptionsApi {
    @GET("youtube/v3/captions")
    suspend fun list(@Header("Authorization") auth: String, @Query("part") part: String = "snippet", @Query("videoId") videoId: String): CaptionsResponse

    @POST("upload/youtube/v3/captions")
    suspend fun upload(@Header("Authorization") auth: String, @Query("uploadType") uploadType: String = "media", @Query("part") part: String = "snippet", @Body body: RequestBody): CaptionTrack

    @DELETE("youtube/v3/captions")
    suspend fun delete(@Header("Authorization") auth: String, @Query("id") captionId: String)
}

class ManageCaptions(private val api: ManageCaptionsApi, private val accessTokenProvider: suspend () -> String) {
    fun listForVideo(videoId: String): Flow<List<CaptionTrack>> = flow {
        emit(api.list("Bearer ${accessTokenProvider()}", videoId = videoId).items)
    }
}

data class CaptionsResponse(val items: List<CaptionTrack> = emptyList())
data class CaptionTrack(val id: String? = null, val snippet: CaptionSnippet? = null)
data class CaptionSnippet(val videoId: String? = null, val language: String? = null, val name: String? = null, val trackKind: String? = null)
