package com.supdevinci.quizz.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.supdevinci.quizz.model.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class QuizzDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: QuizzDatabase? = null

        fun getInstance(context: Context): QuizzDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    QuizzDatabase::class.java,
                    "quizz.db"
                ).build().also { INSTANCE = it }
            }
    }
}
