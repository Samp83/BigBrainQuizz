package com.supdevinci.quizz.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstname: String,
    val lastname: String,
    val score: Int = 0
)

