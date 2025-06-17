package com.supdevinci.quizz.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val firstname: String,
    val lastname: String,
    val score: Int = 0
)
