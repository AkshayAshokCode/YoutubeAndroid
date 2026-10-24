package com.akshayashokcode.youtubeandroid.features.oauthrequired.comments

import app.cash.turbine.test
import com.akshayashokcode.youtubeandroid.core.model.CommentThread
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
class CommentsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<CommentsRepository>()
    private lateinit var viewModel: CommentsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = CommentsViewModel(repository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial postState is Idle`() = assertTrue(viewModel.state.value.postState is UiState.Idle)

    @Test
    fun `post comment success emits Loading then Data`() = runTest {
        every { repository.postComment(any(), any()) } returns
                flowOf(AppResult.Success(CommentThread(id = "ct_1")))

        viewModel.state.test {
            awaitItem() // idle
            viewModel.postComment("videoId", "Hello!")
            val loading = awaitItem()
            assertTrue(loading.postState is UiState.Loading)
            val data = awaitItem()
            assertTrue(data.postState is UiState.Data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `post comment error emits Error`() = runTest {
        every { repository.postComment(any(), any()) } returns
                flowOf(AppResult.Error(RuntimeException("403"), "403"))

        viewModel.state.test {
            awaitItem()
            viewModel.postComment("videoId", "Hello!")
            awaitItem() // loading
            val error = awaitItem()
            assertTrue(error.postState is UiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `delete comment success emits Data and clears postedCommentId`() = runTest {
        every { repository.deleteComment(any()) } returns flowOf(AppResult.Success(Unit))

        viewModel.state.test {
            awaitItem()
            viewModel.deleteComment("ct_1")
            awaitItem() // loading
            val result = awaitItem()
            assertTrue(result.deleteState is UiState.Data)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
