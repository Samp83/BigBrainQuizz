package com.supdevinci.quizz.data.local

import androidx.room.*
import com.supdevinci.quizz.model.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = 1")
    fun getUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(user: UserEntity)

    @Query("UPDATE user SET score = score + 1 WHERE id = 1")
    fun incrementScore()
}
