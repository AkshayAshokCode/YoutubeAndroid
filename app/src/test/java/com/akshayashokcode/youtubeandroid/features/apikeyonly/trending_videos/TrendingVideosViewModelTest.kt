package com.akshayashokcode.youtubeandroid.features.apikeyonly.trending_videos

import app.cash.turbine.test
import com.akshayashokcode.youtubeandroid.core.model.VideoListResponse
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import com.akshayashokcode.youtubeandroid.features.apikeyonly.video_details.VideoDetailsRepository
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
class TrendingVideosViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<VideoDetailsRepository>()
    private lateinit var viewModel: TrendingVideosViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = TrendingVideosViewModel(repository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial state is Idle`() = assertTrue(viewModel.uiState.value is UiState.Idle)

    @Test
    fun `success emits Loading then Data`() = runTest {
        every { repository.getPopularVideos(any(), any(), any()) } returns
                flowOf(AppResult.Success(VideoListResponse(items = emptyList())))

        viewModel.uiState.test {
            assertTrue(awaitItem() is UiState.Idle)
            viewModel.load("US", null)
            assertTrue(awaitItem() is UiState.Loading)
            assertTrue(awaitItem() is UiState.Data)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
