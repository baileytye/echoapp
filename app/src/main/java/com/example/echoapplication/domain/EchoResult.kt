package com.example.echoapplication.domain

sealed interface EchoResult {
    data class Success(val text: String) : EchoResult
    data class Error(val message: String) : EchoResult
}