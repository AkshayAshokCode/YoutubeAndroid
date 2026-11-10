package com.akshayashokcode.youtubeandroid.features.oauthrequired.upload_video

import android.content.Context
import android.net.Uri
import com.akshayashokcode.youtubeandroid.core.model.Video
import com.akshayashokcode.youtubeandroid.core.model.VideoInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.VideoInsertSnippet
import com.akshayashokcode.youtubeandroid.core.model.VideoInsertStatus
import com.akshayashokcode.youtubeandroid.core.network.YouTubeOAuthApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

data class VideoMetadata(
    val title: String,
    val description: String,
    val tags: List<String>,
    val categoryId: String,
    val privacyStatus: String,
    val madeForKids: Boolean,
)

sealed interface UploadEvent {
    data class Progress(val bytesSent: Long, val totalBytes: Long) : UploadEvent
    data class Completed(val video: Video) : UploadEvent
}

/**
 * Uploads videos with the resumable upload protocol:
 * https://developers.google.com/youtube/v3/guides/using_resumable_upload_protocol
 *
 *   1. POST the metadata → the session URI comes back in the Location header.
 *   2. PUT the file in chunks (multiples of 256 KiB) with Content-Range headers.
 *      The server answers 308 after each chunk, and 200/201 with the Video when done.
 *   3. On a network error or 5xx, wait, ask the server how many bytes it has
 *      (`Content-Range: bytes STAR/total`, empty body), and continue from there.
 */
class VideoUploadRepository @Inject constructor(
    private val service: YouTubeOAuthApiService,
    @ApplicationContext private val context: Context,
) {

    fun uploadVideo(uri: Uri, metadata: VideoMetadata): Flow<AppResult<UploadEvent>> =
        uploadVideo(ContentUriUploadSource(context.contentResolver, uri), metadata)
            .flowOn(Dispatchers.IO)

    internal fun uploadVideo(
        source: UploadSource,
        metadata: VideoMetadata,
        chunkSize: Int = CHUNK_SIZE,
    ): Flow<AppResult<UploadEvent>> = flow<AppResult<UploadEvent>> {
        require(metadata.title.isNotBlank()) { "Title is required." }
        val total = source.size
        require(total > 0) { "Couldn't read the video file's size." }

        val sessionUri = startSession(source, metadata, total)
        emit(AppResult.Success(UploadEvent.Progress(0, total)))

        var offset = 0L
        var failures = 0
        var askServerForOffset = false

        while (true) {
            val (contentRange, body) = if (askServerForOffset) {
                "bytes */$total" to ByteArray(0)
            } else {
                val bytes = source.read(offset, minOf(chunkSize.toLong(), total - offset).toInt())
                "bytes $offset-${offset + bytes.size - 1}/$total" to bytes
            }

            val response = try {
                service.uploadVideoChunk(
                    sessionUri = sessionUri,
                    contentRange = contentRange,
                    chunk = body.toRequestBody(source.mimeType.toMediaTypeOrNull()),
                )
            } catch (e: IOException) {
                null // Connection dropped: treat like a 5xx and resume.
            }

            when {
                response != null && response.isSuccessful -> {
                    val video = response.body() ?: error("Upload finished but YouTube returned no video.")
                    emit(AppResult.Success(UploadEvent.Completed(video)))
                    return@flow
                }
                response?.code() == RESUME_INCOMPLETE -> {
                    offset = nextOffset(response)
                    failures = 0
                    askServerForOffset = false
                    emit(AppResult.Success(UploadEvent.Progress(offset, total)))
                }
                response == null || response.code() >= 500 -> {
                    failures++
                    if (failures > MAX_RETRIES) {
                        throw IOException("Upload interrupted. Check your connection and try again.")
                    }
                    delay(RETRY_BASE_DELAY_MS shl (failures - 1)) // 1s, 2s, 4s, 8s, 16s
                    askServerForOffset = true
                }
                response.code() == 404 || response.code() == 410 ->
                    throw IOException("Upload session expired. Start the upload again.")
                else -> throw HttpException(response)
            }
        }
    }.catch { emit(AppResult.Error(it)) }

    private suspend fun startSession(source: UploadSource, metadata: VideoMetadata, total: Long): String {
        val response = service.startVideoUpload(
            contentLength = total,
            contentType = source.mimeType,
            metadata = VideoInsertRequest(
                snippet = VideoInsertSnippet(
                    title = metadata.title,
                    description = metadata.description,
                    tags = metadata.tags,
                    categoryId = metadata.categoryId,
                ),
                status = VideoInsertStatus(
                    privacyStatus = metadata.privacyStatus,
                    selfDeclaredMadeForKids = metadata.madeForKids,
                ),
            ),
        )
        if (!response.isSuccessful) throw HttpException(response)
        return response.headers()["Location"]
            ?: error("YouTube didn't return an upload session URL.")
    }

    /** "Range: bytes=0-524287" means the server has bytes 0..524287, so continue at 524288. */
    private fun nextOffset(response: Response<*>): Long =
        response.headers()["Range"]
            ?.substringAfterLast('-')
            ?.toLongOrNull()
            ?.plus(1)
            ?: 0L

    companion object {
        /** Must be a multiple of 256 KiB. Larger chunks are faster; smaller ones lose less on failure. */
        const val CHUNK_SIZE = 8 * 1024 * 1024
        private const val RESUME_INCOMPLETE = 308
        private const val MAX_RETRIES = 5
        private const val RETRY_BASE_DELAY_MS = 1_000L
    }
}
