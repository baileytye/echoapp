package com.example.echoapplication.domain

import javax.inject.Inject

class GetCharLimitUseCase @Inject constructor(
    private val repository: CharLimitRepository
) {
    suspend operator fun invoke(): CharLimitConfig {
        return repository.getCharLimit()
    }
}
