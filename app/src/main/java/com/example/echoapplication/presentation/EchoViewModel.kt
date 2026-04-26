package com.example.echoapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoapplication.domain.SubmitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EchoUiState(
    val inputText: String = "",
    val outputText: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class EchoViewModel @Inject constructor(
    private val submitEchoUseCase: SubmitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EchoUiState())
    val uiState: StateFlow<EchoUiState> = _uiState.asStateFlow()

    fun onTextChanged(text: String) {
        _uiState.update {
            it.copy(
                inputText = text,
                errorMessage = null
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
        }
    }
}