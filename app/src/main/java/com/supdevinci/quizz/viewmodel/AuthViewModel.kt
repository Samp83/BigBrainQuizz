package com.supdevinci.quizz.viewmodel

import android.app.Application
import android.media.audiofx.AudioEffect.Descriptor
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.quizz.data.local.QuizzDatabase
import com.supdevinci.quizz.data.local.UserPreferences
import com.supdevinci.quizz.model.User
import com.supdevinci.quizz.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application) : AndroidViewModel(application) {

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
        }}
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


    fun registerUser(firstname: String, lastname: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val user = UserEntity(firstname = firstname, lastname = lastname)
                val id = userDao.insert(user).toInt()
                Log.d("AuthViewModel", "User inserted with ID = $id")
                userPrefs.saveUserId(id)
                _currentUser.value = user.copy(id = id)
                _isAuthenticated.value = true
            }
        }
    }


    fun logout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            userPrefs.clearUserId()
            _isAuthenticated.value = false
            _currentUser.value = null
        }}
    }
}
