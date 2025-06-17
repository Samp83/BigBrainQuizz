package com.supdevinci.quizz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.quizz.data.remote.RetrofitInstance
import com.supdevinci.quizz.model.QuizzQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizzViewModel : ViewModel() {

    private val _question = MutableStateFlow<QuizzQuestion?>(null)
    val question: StateFlow<QuizzQuestion?> = _question

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchQuestion() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getQuestion()
                if (response.response_code == 0 && response.results.isNotEmpty()) {
                    _question.value = response.results[0]
                    _error.value = null
                } else {
                    _error.value = "Erreur : réponse invalide"
                }
            } catch (e: Exception) {
                _error.value = "Erreur : ${e.message}"
            }
        }
    }
}
