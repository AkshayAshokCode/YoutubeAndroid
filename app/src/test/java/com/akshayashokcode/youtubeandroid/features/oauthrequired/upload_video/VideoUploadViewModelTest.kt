package com.akshayashokcode.youtubeandroid.features.oauthrequired.upload_video

import android.net.Uri
import com.akshayashokcode.youtubeandroid.core.model.Video
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideoUploadViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<VideoUploadRepository>()
    private val uri = mockk<Uri>()
    private lateinit var viewModel: VideoUploadViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VideoUploadViewModel(repository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun upload() = viewModel.upload(uri, "Title", "", "a, b", "", "private", false)

    @Test
    fun `progress events update the progress fraction`() = runTest {
        val events = MutableSharedFlow<AppResult<UploadEvent>>()
        every { repository.uploadVideo(uri, any()) } returns events

        upload()
        dispatcher.scheduler.advanceUntilIdle()
        events.emit(AppResult.Success(UploadEvent.Progress(5, 10)))
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(0.5f, viewModel.progress.value)
        assertTrue(viewModel.uiState.value is UiState.Loading)
    }

    @Test
    fun `completed upload shows the video and clears progress`() = runTest {
        every { repository.uploadVideo(uri, any()) } returns flowOf(
            AppResult.Success(UploadEvent.Progress(10, 10)),
            AppResult.Success(UploadEvent.Completed(Video(id = "vid_1"))),
        )

        upload()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("vid_1", (viewModel.uiState.value as UiState.Data).value.id)
        assertNull(viewModel.progress.value)
    }

    @Test
    fun `failed upload shows the error and clears progress`() = runTest {
        every { repository.uploadVideo(uri, any()) } returns
            flowOf(AppResult.Error(IllegalStateException("Upload session expired.")))

        upload()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Upload session expired.", (viewModel.uiState.value as UiState.Error).message)
        assertNull(viewModel.progress.value)
    }
}
