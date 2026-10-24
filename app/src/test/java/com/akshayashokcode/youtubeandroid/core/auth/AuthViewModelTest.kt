package com.akshayashokcode.youtubeandroid.core.auth

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<AuthRepository>()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { repository.authState } returns MutableStateFlow(AuthState.SignedOut)
        coEvery { repository.restoreSession() } just runs
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `init restores the previous session`() = runTest {
        dispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { repository.restoreSession() }
    }

    @Test
    fun `signIn emits consent request when Google needs UI`() = runTest {
        val pendingIntent = mockk<PendingIntent>()
        coEvery { repository.signIn() } returns pendingIntent

        viewModel.consentRequests.test {
            viewModel.signIn()
            assertSame(pendingIntent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `signIn emits nothing when token is granted silently`() = runTest {
        coEvery { repository.signIn() } returns null

        viewModel.consentRequests.test {
            viewModel.signIn()
            dispatcher.scheduler.advanceUntilIdle()
            expectNoEvents()
        }
    }

    @Test
    fun `consent result OK is passed to repository`() {
        val data = mockk<Intent>()
        every { repository.onConsentResult(data) } just runs

        viewModel.onConsentResult(Activity.RESULT_OK, data)

        verify { repository.onConsentResult(data) }
    }

    @Test
    fun `consent result cancelled marks sign-in cancelled`() {
        every { repository.onConsentCancelled() } just runs

        viewModel.onConsentResult(Activity.RESULT_CANCELED, null)

        verify { repository.onConsentCancelled() }
    }

    @Test
    fun `signOut delegates to repository`() = runTest {
        coEvery { repository.signOut() } just runs

        viewModel.signOut()
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.signOut() }
    }
}
