package com.supdevinci.quizz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.quizz.data.local.QuizzDatabase
import com.supdevinci.quizz.model.UserEntity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = QuizzDatabase.getInstance(application).userDao()

    val users: StateFlow<List<UserEntity>> = userDao.getAllUsersSortedByScore()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
