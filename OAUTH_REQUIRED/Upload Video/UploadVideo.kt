package examples.oauth_required.uploadvideo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UploadVideoApi {
    @POST("upload/youtube/v3/videos")
    suspend fun uploadMetadata(
        @Header("Authorization") authorization: String,
        @Header("X-Upload-Content-Type") contentType: String,
        @Query("uploadType") uploadType: String = "resumable",
        @Query("part") part: String = "snippet,status",
        @Body metadata: UploadVideoRequest,
    ): retrofit2.Response<Unit>
}

class UploadVideo(private val api: UploadVideoApi, private val accessTokenProvider: suspend () -> String) {
    fun startResumableUpload(request: UploadVideoRequest, contentType: String): Flow<String> = flow {
        val response = api.uploadMetadata(
            authorization = "Bearer ${accessTokenProvider()}",
            contentType = contentType,
            metadata = request,
        )
        val uploadUrl = response.headers()["Location"]
        require(!uploadUrl.isNullOrBlank()) { "YouTube did not return a resumable upload URL." }
        emit(uploadUrl)
    }
}

data class UploadVideoRequest(val snippet: UploadSnippet, val status: UploadStatus)
data class UploadSnippet(val title: String, val description: String, val tags: List<String> = emptyList(), val categoryId: String? = null)
data class UploadStatus(val privacyStatus: String = "unlisted", val selfDeclaredMadeForKids: Boolean = false)
