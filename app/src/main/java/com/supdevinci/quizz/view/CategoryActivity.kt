package com.supdevinci.quizz.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.supdevinci.quizz.ui.theme.QuizzTheme
import com.supdevinci.quizz.viewmodel.CategoryViewModel

class CategoryActivity : ComponentActivity() {

    private val categoryViewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        categoryViewModel.fetchCategories()

        setContent {
            QuizzTheme {
                val categories by categoryViewModel.categories.collectAsState()
                val error by categoryViewModel.error.collectAsState()

                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) {

                    Text("Choose a category", style = MaterialTheme.typography.titleLarge)

                    Spacer(Modifier.height(16.dp))

                    error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                    }

                    categories.forEach { (id, name) ->
                        Text(
                            text = name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(this@CategoryActivity, QuizzActivity::class.java)
                                    intent.putExtra("categoryId", id)
                                    intent.putExtra("categoryName", name)
                                    startActivity(intent)
                                }
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
