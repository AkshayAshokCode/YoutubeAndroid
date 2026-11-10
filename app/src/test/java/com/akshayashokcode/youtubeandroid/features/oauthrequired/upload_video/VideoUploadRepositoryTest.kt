package com.akshayashokcode.youtubeandroid.features.oauthrequired.upload_video

import android.content.Context
import com.akshayashokcode.youtubeandroid.core.model.Video
import com.akshayashokcode.youtubeandroid.core.network.YouTubeOAuthApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.Headers.Companion.headersOf
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class VideoUploadRepositoryTest {

    private val service = mockk<YouTubeOAuthApiService>()
    private val repository = VideoUploadRepository(service, mockk<Context>(relaxed = true))

    private val source = object : UploadSource {
        private val bytes = ByteArray(10) { it.toByte() }
        override val size = bytes.size.toLong()
        override val mimeType = "video/mp4"
        override fun read(offset: Long, length: Int) =
            bytes.copyOfRange(offset.toInt(), offset.toInt() + length)
    }

    private val metadata = VideoMetadata(
        title = "Test \"quoted\" title",
        description = "",
        tags = emptyList(),
        categoryId = "22",
        privacyStatus = "private",
        madeForKids = false,
    )

    private fun upload() = repository.uploadVideo(source, metadata, chunkSize = 4)

    private fun givenSessionStarts() {
        coEvery { service.startVideoUpload(any(), 10, "video/mp4", any()) } returns
            Response.success(Unit, headersOf("Location", SESSION))
    }

    private fun chunk(range: String) = coEvery { service.uploadVideoChunk(SESSION, range, any()) }

    private fun resumeIncomplete(serverHasThrough: Long?): Response<Video> {
        val raw = okhttp3.Response.Builder()
            .code(308)
            .message("Resume Incomplete")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(SESSION).build())
            .apply { serverHasThrough?.let { header("Range", "bytes=0-$it") } }
            .build()
        return Response.error("".toResponseBody(), raw)
    }

    @Test
    fun `uploads in chunks and reports progress until complete`() = runTest {
        givenSessionStarts()
        chunk("bytes 0-3/10") returns resumeIncomplete(3)
        chunk("bytes 4-7/10") returns resumeIncomplete(7)
        chunk("bytes 8-9/10") returns Response.success(Video(id = "vid_1"))

        val events = upload().toList().map { (it as AppResult.Success).data }

        assertEquals(
            listOf(
                UploadEvent.Progress(0, 10),
                UploadEvent.Progress(4, 10),
                UploadEvent.Progress(8, 10),
                UploadEvent.Completed(Video(id = "vid_1")),
            ),
            events,
        )
    }

    @Test
    fun `resumes from the server's offset after a dropped connection`() = runTest {
        givenSessionStarts()
        chunk("bytes 0-3/10") throws IOException("connection reset")
        // Server only stored bytes 0-1 before the connection dropped.
        chunk("bytes */10") returns resumeIncomplete(1)
        chunk("bytes 2-5/10") returns resumeIncomplete(5)
        chunk("bytes 6-9/10") returns Response.success(Video(id = "vid_1"))

        val last = upload().toList().last()

        assertEquals(AppResult.Success(UploadEvent.Completed(Video(id = "vid_1"))), last)
        coVerify(exactly = 1) { service.uploadVideoChunk(SESSION, "bytes */10", any()) }
    }

    @Test
    fun `expired session fails with a clear message`() = runTest {
        givenSessionStarts()
        chunk("bytes 0-3/10") returns Response.error(404, "".toResponseBody())

        val last = upload().toList().last()

        assertTrue(last is AppResult.Error)
        assertEquals("Upload session expired. Start the upload again.", (last as AppResult.Error).message)
    }

    @Test
    fun `gives up after repeated network failures`() = runTest {
        givenSessionStarts()
        chunk("bytes 0-3/10") throws IOException("offline")
        chunk("bytes */10") throws IOException("offline")

        val last = upload().toList().last()

        assertEquals(
            "Upload interrupted. Check your connection and try again.",
            (last as AppResult.Error).message,
        )
    }

    @Test
    fun `blank title fails before contacting YouTube`() = runTest {
        val result = repository.uploadVideo(source, metadata.copy(title = " "), chunkSize = 4)
            .toList().single()

        assertEquals("Title is required.", (result as AppResult.Error).message)
        coVerify(exactly = 0) { service.startVideoUpload(any(), any(), any(), any()) }
    }

    private companion object {
        const val SESSION = "https://www.googleapis.com/upload/youtube/v3/videos?upload_id=abc"
    }
}
