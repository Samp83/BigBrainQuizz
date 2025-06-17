package com.supdevinci.quizz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.quizz.data.local.QuizzDatabase
import com.supdevinci.quizz.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = QuizzDatabase.getInstance(application).userDao()

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {_user.value = userDao.getUser()}

        }
    }

    fun saveUser(firstname: String, lastname: String) {
        val user = UserEntity(firstname = firstname, lastname = lastname)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
            userDao.insertOrUpdate(user)
            _user.value = user
        }}
    }

    fun incrementScore() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
            userDao.incrementScore()
            _user.value = userDao.getUser()
        }}
    }
}
