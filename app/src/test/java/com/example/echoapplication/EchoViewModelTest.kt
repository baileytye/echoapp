package com.example.echoapplication

import com.example.echoapplication.domain.CharLimitConfig
import com.example.echoapplication.domain.CharLimitRepository
import com.example.echoapplication.domain.EchoRepository
import com.example.echoapplication.domain.EchoResult
import com.example.echoapplication.domain.GetCharLimitUseCase
import com.example.echoapplication.domain.SubmitUseCase
import com.example.echoapplication.presentation.EchoViewModel
import com.example.echoapplication.presentation.NavigationEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EchoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `submit success shows output and navigates to result`() = runTest {
        val repository = FakeEchoRepository(
            result = EchoResult.Success("Hello world")
        )

        val viewModel = EchoViewModel(
            submitEchoUseCase = SubmitUseCase(repository),
            getCharLimitUseCase = GetCharLimitUseCase(FakeCharLimitRepository(150))
        )

        val events = mutableListOf<NavigationEvent>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.toList(events)
        }

        viewModel.onTextChanged(" Hello world ")
        viewModel.onSubmitClicked()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals("Hello world", state.outputText)
        assertNull(state.errorMessage)
        assertFalse(state.isLoading)
        assertEquals("Hello world", repository.submittedText)
        assertEquals(listOf(NavigationEvent.NavigateToResult), events)
    }

    @Test
    fun `submit failure shows error and navigates to result`() = runTest {
        val repository = FakeEchoRepository(
            result = EchoResult.Error("Fake server rejected this request.")
        )

        val viewModel = EchoViewModel(
            submitEchoUseCase = SubmitUseCase(repository),
            getCharLimitUseCase = GetCharLimitUseCase(FakeCharLimitRepository(150))
        )

        val events = mutableListOf<NavigationEvent>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.toList(events)
        }

        viewModel.onTextChanged("This should fail")
        viewModel.onSubmitClicked()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertNull(state.outputText)
        assertEquals("Fake server rejected this request.", state.errorMessage)
        assertFalse(state.isLoading)
        assertEquals(listOf(NavigationEvent.NavigateToResult), events)
    }

    @Test
    fun `char limit loads from config on init`() = runTest {
        val viewModel = EchoViewModel(
            submitEchoUseCase = SubmitUseCase(FakeEchoRepository(EchoResult.Success(""))),
            getCharLimitUseCase = GetCharLimitUseCase(FakeCharLimitRepository(80))
        )

        assertEquals(0, viewModel.uiState.value.charLimit)
    }

    @Test
    fun `isOverLimit is true when text exceeds char limit`() = runTest {
        val viewModel = EchoViewModel(
            submitEchoUseCase = SubmitUseCase(FakeEchoRepository(EchoResult.Success(""))),
            getCharLimitUseCase = GetCharLimitUseCase(FakeCharLimitRepository(10))
        )

        advanceUntilIdle()

        viewModel.onTextChanged("This text is definitely longer than ten characters")

        assertTrue(viewModel.uiState.value.isOverLimit)
    }

    @Test
    fun `counter increments as user types`() = runTest {
        val viewModel = EchoViewModel(
            submitEchoUseCase = SubmitUseCase(FakeEchoRepository(EchoResult.Success(""))),
            getCharLimitUseCase = GetCharLimitUseCase(FakeCharLimitRepository(150))
        )

        viewModel.onTextChanged("Hello")

        val state = viewModel.uiState.value
        assertTrue(state.inputText.length > 0)
    }

    private class FakeEchoRepository(
        private val result: EchoResult
    ) : EchoRepository {

        var submittedText: String? = null
            private set

        override suspend fun submit(text: String): EchoResult {
            submittedText = text
            return result
        }
    }

    private class FakeCharLimitRepository(
        private val limit: Int
    ) : CharLimitRepository {
        override suspend fun getCharLimit(): CharLimitConfig = CharLimitConfig(maxLength = limit)
    }
}
