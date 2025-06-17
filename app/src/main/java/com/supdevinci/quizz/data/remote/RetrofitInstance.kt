package com.supdevinci.quizz.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: com.supdevinci.quizz.service.QuizzApi by lazy {
        retrofit.create(com.supdevinci.quizz.service.QuizzApi::class.java)
    }
}
