package com.supdevinci.quizz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.quizz.data.local.QuizzDatabase
import com.supdevinci.quizz.data.local.UserPreferences
import com.supdevinci.quizz.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = QuizzDatabase.getInstance(application).userDao()

    private val userPrefs = UserPreferences(application)

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    val users: StateFlow<List<UserEntity>> = userDao.getAllUsersSortedByScore()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    fun logout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                userPrefs.clearUserId()
                _isAuthenticated.value = false
                _currentUser.value = null
            }
        }
    }
}
