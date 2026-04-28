package com.example.echoapplication.domain

data class CharLimitConfig(
    val maxLength: Int
) {
    fun isExceeded(text: String): Boolean = text.length > maxLength
}
