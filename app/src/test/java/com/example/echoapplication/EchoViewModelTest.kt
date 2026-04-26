package com.example.echoapplication

import com.example.echoapplication.domain.EchoRepository
import com.example.echoapplication.domain.EchoResult
import com.example.echoapplication.domain.SubmitUseCase
import com.example.echoapplication.presentation.EchoViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EchoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `submit success shows echoed text`() = runTest {
        val repository = FakeEchoRepository(
            result = EchoResult.Success("Text input")
        )

        val viewModel = EchoViewModel(
            submitEchoUseCase = SubmitUseCase(repository)
        )

        viewModel.onTextChanged("Text input")
        viewModel.onSubmitClicked()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals("Text input", state.outputText)
        assertNull(state.errorMessage)
        assertFalse(state.isLoading)
        assertEquals("Text input", repository.submittedText)
    }

    @Test
    fun `submit failure shows error message`() = runTest {
        val repository = FakeEchoRepository(
            result = EchoResult.Error("Server validation failed. Please enter valid text.")
        )

        val viewModel = EchoViewModel(
            submitEchoUseCase = SubmitUseCase(repository)
        )

        viewModel.onTextChanged("fail")
        viewModel.onSubmitClicked()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertNull(state.outputText)
        assertEquals(
            "Server validation failed. Please enter valid text.",
            state.errorMessage
        )
        assertFalse(state.isLoading)
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