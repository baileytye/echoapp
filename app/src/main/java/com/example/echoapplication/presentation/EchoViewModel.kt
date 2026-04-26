package com.example.echoapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoapplication.domain.EchoResult
import com.example.echoapplication.domain.SubmitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EchoUiState(
    val inputText: String = "",
    val outputText: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) {
    val hasResult: Boolean
        get() = outputText != null || errorMessage != null
}

@HiltViewModel
class EchoViewModel @Inject constructor(
    private val submitEchoUseCase: SubmitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EchoUiState())
    val uiState: StateFlow<EchoUiState> = _uiState.asStateFlow()

    private val navigationChannel = Channel<NavigationEvent>()
    val navigationEvents = navigationChannel.receiveAsFlow()

    fun onTextChanged(text: String) {
        _uiState.update {
            it.copy(
                inputText = text,
                errorMessage = null,
                outputText = null
            )
        }
    }

    fun onSubmitClicked() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    outputText = null,
                    errorMessage = null
                )
            }

            when (val result = submitEchoUseCase(_uiState.value.inputText)) {
                is EchoResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            outputText = result.text,
                            errorMessage = null
                        )
                    }
                }

                is EchoResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            outputText = null,
                            errorMessage = result.message
                        )
                    }
                }
            }

            navigationChannel.send(NavigationEvent.NavigateToResult)
        }
    }

    fun onReturnToInputClicked() {
        _uiState.update {
            it.copy(
                isLoading = false,
                outputText = null,
                errorMessage = null
            )
        }
    }
}