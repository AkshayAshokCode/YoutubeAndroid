package com.akshayashokcode.youtubeandroid.features.apikeyonly.search

import app.cash.turbine.test
import com.akshayashokcode.youtubeandroid.core.model.SearchListResponse
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<SearchRepository>()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertTrue(viewModel.uiState.value is UiState.Idle)
    }

    @Test
    fun `blank query emits Error`() = runTest {
        viewModel.search("")
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun `successful search emits Loading then Data`() = runTest {
        val response = SearchListResponse(items = emptyList())
        every { repository.search(any(), any(), any(), any()) } returns
                flowOf(AppResult.Success(response))

        viewModel.uiState.test {
            assertEquals(UiState.Idle, awaitItem())
            viewModel.search("android")
            assertEquals(UiState.Loading, awaitItem())
            val data = awaitItem()
            assertTrue(data is UiState.Data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `failed search emits Loading then Error`() = runTest {
        every { repository.search(any(), any(), any(), any()) } returns
                flowOf(AppResult.Error(RuntimeException("Network error"), "Network error"))

        viewModel.uiState.test {
            assertEquals(UiState.Idle, awaitItem())
            viewModel.search("android")
            assertEquals(UiState.Loading, awaitItem())
            val error = awaitItem()
            assertTrue(error is UiState.Error)
            assertEquals("Network error", (error as UiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
