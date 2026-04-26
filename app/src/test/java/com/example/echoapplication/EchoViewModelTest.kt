package com.example.echoapplication

import com.example.echoapplication.domain.EchoRepository
import com.example.echoapplication.domain.EchoResult
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
            submitEchoUseCase = SubmitUseCase(repository)
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
            submitEchoUseCase = SubmitUseCase(repository)
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
}