package com.example.echoapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoapplication.domain.EchoResult
import com.example.echoapplication.domain.GetCharLimitUseCase
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
    val isLoading: Boolean = false,
    val charLimit: Int = 0,
    val isOverLimit: Boolean = false
)

@HiltViewModel
class EchoViewModel @Inject constructor(
    private val submitEchoUseCase: SubmitUseCase,
    private val getCharLimitUseCase: GetCharLimitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EchoUiState())
    val uiState: StateFlow<EchoUiState> = _uiState.asStateFlow()

    private val navigationChannel = Channel<NavigationEvent>()
    val navigationEvents = navigationChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            try {
                val config = getCharLimitUseCase()
                _uiState.update { it.copy(charLimit = config.maxLength) }
            } catch (e: Exception) { }
        }
    }

    fun onTextChanged(text: String) {
        val limit = _uiState.value.charLimit
        _uiState.update {
            it.copy(
                inputText = text,
                errorMessage = null,
                outputText = null,
                isOverLimit = text.length > limit
            )
        }
    }

    fun onSubmitClicked() {
        if (_uiState.value.isLoading) return

        val state = _uiState.value
        if (state.inputText.trim().isEmpty()) return
        if (state.isOverLimit) return

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
}
