package com.supdevinci.quizz.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.supdevinci.quizz.ui.theme.QuizzTheme
import com.supdevinci.quizz.viewmodel.AuthViewModel

class HomeActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            QuizzTheme {
                val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
                var firstname by remember { mutableStateOf("") }
                var lastname by remember { mutableStateOf("") }

                if (isAuthenticated) {
                    LaunchedEffect(Unit) {
                        startActivity(Intent(this@HomeActivity, CategoryActivity::class.java))
                        finish()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Welcome", style = MaterialTheme.typography.headlineMedium)

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = firstname,
                            onValueChange = { firstname = it },
                            label = { Text("Firstname") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = lastname,
                            onValueChange = { lastname = it },
                            label = { Text("Lastname") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (firstname.isNotBlank() && lastname.isNotBlank()) {
                                    authViewModel.registerUser(firstname, lastname)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start quizz")
                        }
                    }
                }
            }
        }
    }
}
