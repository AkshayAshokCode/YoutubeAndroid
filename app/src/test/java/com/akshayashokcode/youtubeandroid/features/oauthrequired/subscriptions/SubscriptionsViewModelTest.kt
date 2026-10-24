package com.akshayashokcode.youtubeandroid.features.oauthrequired.subscriptions

import app.cash.turbine.test
import com.akshayashokcode.youtubeandroid.core.model.Subscription
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
class SubscriptionsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<SubscriptionsRepository>()
    private lateinit var viewModel: SubscriptionsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = SubscriptionsViewModel(repository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial state is Idle with no subscriptionId`() {
        assertTrue(viewModel.state.value.subscribeState is UiState.Idle)
        assertEquals(null, viewModel.state.value.subscriptionId)
    }

    @Test
    fun `subscribe success stores subscriptionId`() = runTest {
        every { repository.subscribe(any()) } returns
                flowOf(AppResult.Success(Subscription(id = "sub_1")))

        viewModel.state.test {
            awaitItem()
            viewModel.subscribe("UCxxxxxx")
            awaitItem() // loading
            val result = awaitItem()
            assertTrue(result.subscribeState is UiState.Data)
            assertEquals("sub_1", result.subscriptionId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `unsubscribe success clears subscriptionId`() = runTest {
        every { repository.subscribe(any()) } returns
                flowOf(AppResult.Success(Subscription(id = "sub_1")))
        every { repository.unsubscribe(any()) } returns flowOf(AppResult.Success(Unit))

        viewModel.subscribe("UCxxxxxx")
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()
            viewModel.unsubscribe("sub_1")
            awaitItem() // loading
            val result = awaitItem()
            assertTrue(result.unsubscribeState is UiState.Data)
            assertEquals(null, result.subscriptionId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
