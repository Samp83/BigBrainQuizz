package com.supdevinci.quizz.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.supdevinci.quizz.model.UserEntity
import com.supdevinci.quizz.ui.theme.QuizzTheme
import com.supdevinci.quizz.viewmodel.AuthViewModel
import com.supdevinci.quizz.viewmodel.LeaderboardViewModel

class LeaderboardActivity : ComponentActivity() {

    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuizzTheme {
                val users by leaderboardViewModel.users.collectAsState()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Classement") },
                            actions = {
                                TextButton(onClick = {
                                    authViewModel.logout()
                                    startActivity(Intent(this@LeaderboardActivity, HomeActivity::class.java))
                                    finish()
                                }) {
                                    Text("Déconnexion")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                    ) {
                        LazyColumn {
                            itemsIndexed(users) { index, user ->
                                LeaderboardItem(rank = index + 1, user = user)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(rank: Int, user: UserEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$rank. ${user.firstname} ${user.lastname} ")
            Text("Score: ${user.score}")
        }
    }
}
