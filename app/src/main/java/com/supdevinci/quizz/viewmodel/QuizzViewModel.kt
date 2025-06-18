package com.supdevinci.quizz.viewmodel

import androidx.lifecycle.viewModelScope
import com.supdevinci.quizz.data.local.QuizzDatabase
import com.supdevinci.quizz.data.local.UserPreferences
import com.supdevinci.quizz.data.remote.RetrofitInstance
import com.supdevinci.quizz.model.QuizzQuestion
import com.supdevinci.quizz.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.supdevinci.quizz.utils.Base64Utils

class QuizzViewModel(application: Application) : AndroidViewModel(application) {

    private var sessionToken: String? = null
    private var currentCategoryId: Int? = null

    private val _question = MutableStateFlow<QuizzQuestion?>(null)
    val question: StateFlow<QuizzQuestion?> = _question

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _tokenReady = MutableStateFlow(false)
    val tokenReady: StateFlow<Boolean> = _tokenReady

    private val userDao = QuizzDatabase.getInstance(application).userDao()
    private val userPrefs = UserPreferences(application)

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    init {
        checkUser()
    }
    fun checkUser() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val userId = userPrefs.getUserId()
                if (userId != null) {
                    val user = userDao.getUserById(userId)
                    _currentUser.value = user
                    _isAuthenticated.value = user != null
                } else {
                    _isAuthenticated.value = false
                }
            }
        }
    }

    fun incrementScore(user: UserEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userDao.incrementScore(user.id)
                val updatedUser = userDao.getUserById(user.id)
                if (updatedUser != null) {
                    _currentUser.value = updatedUser
                }

            }
        }
    }

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
                            val raw = response.results[0]
                            val decoded = QuizzQuestion(
                                category = Base64Utils.decode(raw.category),
                                type = Base64Utils.decode(raw.type),
                                difficulty = Base64Utils.decode(raw.difficulty),
                                question = Base64Utils.decode(raw.question),
                                correct_answer = Base64Utils.decode(raw.correct_answer),
                                incorrect_answers = raw.incorrect_answers.map { Base64Utils.decode(it) }
                            )
                            _question.value = decoded
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
