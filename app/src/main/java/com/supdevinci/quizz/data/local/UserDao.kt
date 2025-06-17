package com.supdevinci.quizz.data.local

import androidx.room.*
import com.supdevinci.quizz.model.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUserById(userId: Int): UserEntity?

    @Insert
    fun insert(user: UserEntity): Long

    @Query("UPDATE user SET score = score + 1 WHERE id = :userId")
    fun incrementScore(userId: Int)

    @Query("SELECT * FROM user ORDER BY score DESC")
    fun getAllUsersSortedByScore(): kotlinx.coroutines.flow.Flow<List<UserEntity>>

}

