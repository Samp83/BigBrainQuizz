package com.supdevinci.quizz.model

data class TokenResponse(
    val response_code: Int,
    val response_message: String,
    val token: String
)
