package com.supdevinci.quizz.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
    @PrimaryKey val id: Int = 1,
    val users : List<User>
)