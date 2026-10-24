package com.akshayashokcode.youtubeandroid.features.oauthrequired.upload_video

import com.akshayashokcode.youtubeandroid.core.model.Video
import com.akshayashokcode.youtubeandroid.core.network.YouTubeOAuthApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

data class VideoMetadata(
    val title: String,
    val description: String,
    val tags: List<String>,
    val categoryId: String,
    val privacyStatus: String,
    val madeForKids: Boolean,
)

class VideoUploadRepository @Inject constructor(
    private val service: YouTubeOAuthApiService,
) {
    /**
     * Uploads a video using the multipart upload endpoint.
     * For production use with large files, replace with the resumable upload protocol:
     * https://developers.google.com/youtube/v3/guides/using_resumable_upload_protocol
     */
    fun uploadVideo(file: File, metadata: VideoMetadata): Flow<AppResult<Video>> = flow {
        emit(
            runAppCatching {
                require(file.exists()) { "Video file not found." }
                require(metadata.title.isNotBlank()) { "Title is required." }

                val snippetJson = buildString {
                    append("{\"title\":\"${metadata.title}\",")
                    append("\"description\":\"${metadata.description}\",")
                    append("\"tags\":[${metadata.tags.joinToString(",") { "\"$it\"" }}],")
                    append("\"categoryId\":\"${metadata.categoryId}\"}")
                }
                val statusJson = buildString {
                    append("{\"privacyStatus\":\"${metadata.privacyStatus}\",")
                    append("\"selfDeclaredMadeForKids\":${metadata.madeForKids}}")
                }

                val snippetBody = snippetJson.toRequestBody("application/json".toMediaTypeOrNull())
                val statusBody = statusJson.toRequestBody("application/json".toMediaTypeOrNull())
                val videoPart = MultipartBody.Part.createFormData(
                    "video",
                    file.name,
                    file.asRequestBody("video/*".toMediaTypeOrNull()),
                )

                service.uploadVideo(
                    snippet = snippetBody,
                    status = statusBody,
                    video = videoPart,
                )
            }
        )
    }
}
