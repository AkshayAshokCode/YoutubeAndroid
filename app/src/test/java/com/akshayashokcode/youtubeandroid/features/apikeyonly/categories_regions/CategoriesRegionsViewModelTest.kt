package com.akshayashokcode.youtubeandroid.features.apikeyonly.categories_regions

import app.cash.turbine.test
import com.akshayashokcode.youtubeandroid.core.model.RegionListResponse
import com.akshayashokcode.youtubeandroid.core.model.VideoCategoryListResponse
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
class CategoriesRegionsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<CategoriesRegionsRepository>()
    private lateinit var viewModel: CategoriesRegionsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = CategoriesRegionsViewModel(repository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial state has Idle for both sections`() {
        assertTrue(viewModel.state.value.categories is UiState.Idle)
        assertTrue(viewModel.state.value.regions is UiState.Idle)
    }

    @Test
    fun `load success emits Data for both sections`() = runTest {
        every { repository.getVideoCategories(any()) } returns
                flowOf(AppResult.Success(VideoCategoryListResponse(items = emptyList())))
        every { repository.getSupportedRegions(any()) } returns
                flowOf(AppResult.Success(RegionListResponse(items = emptyList())))

        viewModel.state.test {
            awaitItem() // idle
            viewModel.load("US", null)
            // Loading states and Data states follow; just check final
            val final = awaitItem()
            // Either loading or data; advance until both are data
            cancelAndIgnoreRemainingEvents()
        }
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.categories is UiState.Data)
        assertTrue(viewModel.state.value.regions is UiState.Data)
    }
}
