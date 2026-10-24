package com.akshayashokcode.youtubeandroid.features.apikeyonly.channel_info

import app.cash.turbine.test
import com.akshayashokcode.youtubeandroid.core.model.ChannelListResponse
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
class ChannelInfoViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<ChannelInfoRepository>()
    private lateinit var viewModel: ChannelInfoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = ChannelInfoViewModel(repository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial state is Idle`() = assertTrue(viewModel.uiState.value is UiState.Idle)

    @Test
    fun `blank input emits Error`() = runTest {
        viewModel.load("")
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun `success emits Loading then Data`() = runTest {
        every { repository.getChannelsById(any()) } returns
                flowOf(AppResult.Success(ChannelListResponse(items = emptyList())))

        viewModel.uiState.test {
            assertTrue(awaitItem() is UiState.Idle)
            viewModel.load("UCxxxxxx")
            assertTrue(awaitItem() is UiState.Loading)
            assertTrue(awaitItem() is UiState.Data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error emits Loading then Error`() = runTest {
        every { repository.getChannelsById(any()) } returns
                flowOf(AppResult.Error(RuntimeException("fail"), "fail"))

        viewModel.uiState.test {
            assertTrue(awaitItem() is UiState.Idle)
            viewModel.load("UCxxxxxx")
            assertTrue(awaitItem() is UiState.Loading)
            assertTrue(awaitItem() is UiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
