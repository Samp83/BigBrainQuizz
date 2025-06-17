package com.supdevinci.quizz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL

class CategoryViewModel : ViewModel() {

    private val _categories = MutableStateFlow<List<Pair<Int, String>>>(emptyList())
    val categories: StateFlow<List<Pair<Int, String>>> = _categories

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = URL("https://opentdb.com/api_category.php").readText()
                val json = JSONObject(response)
                val jsonArray = json.getJSONArray("trivia_categories")

                val list = mutableListOf<Pair<Int, String>>()
                for (i in 0 until jsonArray.length()) {
                    val category = jsonArray.getJSONObject(i)
                    val id = category.getInt("id")
                    val name = category.getString("name")
                    list.add(id to name)
                }

                _categories.value = list
                _error.value = null

            } catch (e: Exception) {
                _error.value = "Erreur de chargement : ${e.message}"
            }
        }
    }
}
