package com.akshayashokcode.youtubeandroid.features.apikeyonly.video_details

import app.cash.turbine.test
import com.akshayashokcode.youtubeandroid.core.model.VideoListResponse
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideoDetailsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<VideoDetailsRepository>()
    private lateinit var viewModel: VideoDetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VideoDetailsViewModel(repository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial state is Idle`() = assertTrue(viewModel.uiState.value is UiState.Idle)

    @Test
    fun `blank input emits Error`() = runTest {
        viewModel.load("   ")
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun `success emits Loading then Data`() = runTest {
        val response = VideoListResponse(items = emptyList())
        every { repository.getVideoDetails(any()) } returns flowOf(AppResult.Success(response))

        viewModel.uiState.test {
            assertTrue(awaitItem() is UiState.Idle)
            viewModel.load("abc123")
            assertTrue(awaitItem() is UiState.Loading)
            assertTrue(awaitItem() is UiState.Data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error emits Loading then Error`() = runTest {
        every { repository.getVideoDetails(any()) } returns
                flowOf(AppResult.Error(RuntimeException("fail"), "fail"))

        viewModel.uiState.test {
            assertTrue(awaitItem() is UiState.Idle)
            viewModel.load("abc123")
            assertTrue(awaitItem() is UiState.Loading)
            val state = awaitItem()
            assertTrue(state is UiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
