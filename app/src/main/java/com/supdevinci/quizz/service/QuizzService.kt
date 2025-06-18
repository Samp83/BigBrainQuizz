package com.supdevinci.quizz.service

import com.supdevinci.quizz.model.QuizzResponse
import com.supdevinci.quizz.model.TokenResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizzApi {

    @GET("api.php")
    suspend fun getQuestion(
        @Query("token") token: String,
        @Query("category") categoryId: Int,
        @Query("amount") amount: Int = 1,
        @Query("encode") encode: String = "base64"
    ): QuizzResponse

    @GET("api_token.php?command=request")
    suspend fun requestToken(): TokenResponse

    @GET("api_token.php?command=reset")
    suspend fun resetToken(@Query("token") token: String): TokenResponse
}

