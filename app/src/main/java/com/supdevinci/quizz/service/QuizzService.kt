package com.supdevinci.quizz.service

import com.supdevinci.quizz.model.QuizzResponse
import retrofit2.http.GET

interface QuizzApi{
    @GET("api.php?amount=1")
    suspend fun getQuestion() : QuizzResponse
}