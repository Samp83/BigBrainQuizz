package com.supdevinci.quizz.data.local



import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val USER_ID_KEY = intPreferencesKey("user_id")
    }

    suspend fun saveUserId(id: Int) {
        context.dataStore.edit { it[USER_ID_KEY] = id }
    }

    suspend fun getUserId(): Int? {
        val prefs = context.dataStore.data.first()
        return prefs[USER_ID_KEY]
    }

    suspend fun clearUserId() {
        context.dataStore.edit { it.remove(USER_ID_KEY) }
    }
}
