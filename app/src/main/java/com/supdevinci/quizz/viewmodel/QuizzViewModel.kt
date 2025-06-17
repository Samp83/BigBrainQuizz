package com.supdevinci.quizz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.quizz.data.remote.RetrofitInstance
import com.supdevinci.quizz.model.QuizzQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizzViewModel : ViewModel() {

    private var sessionToken: String? = null
    private var currentCategoryId: Int? = null

    private val _question = MutableStateFlow<QuizzQuestion?>(null)
    val question: StateFlow<QuizzQuestion?> = _question

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _tokenReady = MutableStateFlow(false)
    val tokenReady: StateFlow<Boolean> = _tokenReady

    fun initialize(categoryId: Int) {
        currentCategoryId = categoryId
        viewModelScope.launch {
            try {
                val tokenResponse = RetrofitInstance.api.requestToken()
                if (tokenResponse.response_code == 0) {
                    sessionToken = tokenResponse.token
                    _tokenReady.value = true
                } else {
                    _error.value = "Erreur récupération token"
                }
            } catch (e: Exception) {
                _error.value = "Erreur réseau token : ${e.message}"
            }
        }
    }

    fun fetchQuestion() {
        viewModelScope.launch {
            try {
                val token = sessionToken ?: return@launch
                val categoryId = currentCategoryId ?: return@launch

                val response = RetrofitInstance.api.getQuestion(token, categoryId)
                when (response.response_code) {
                    0 -> {
                        if (response.results.isNotEmpty()) {
                            _question.value = response.results[0]
                            _error.value = null
                        }
                    }
                    4 -> {
                        resetToken()
                        fetchQuestion()
                    }
                    else -> {
                        _error.value = "Erreur API: ${response.response_code}"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Erreur réseau : ${e.message}"
            }
        }
    }

    private suspend fun resetToken() {
        try {
            val resetResponse = RetrofitInstance.api.resetToken(sessionToken!!)
            if (resetResponse.response_code != 0) {
                _error.value = "Erreur reset token"
            }
        } catch (e: Exception) {
            _error.value = "Erreur réseau reset : ${e.message}"
        }
    }
}
