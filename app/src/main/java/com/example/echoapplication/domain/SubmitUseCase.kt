package com.example.echoapplication.domain

import javax.inject.Inject

class SubmitUseCase @Inject constructor(
    private val repository: EchoRepository
) {
    suspend operator fun invoke(input: String): EchoResult {
        return repository.submit(input.trim())
    }
}