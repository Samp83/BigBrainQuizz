package com.supdevinci.quizz.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.supdevinci.quizz.model.UserEntity
import com.supdevinci.quizz.ui.theme.QuizzTheme
import com.supdevinci.quizz.viewmodel.LeaderboardViewModel

class LeaderboardActivity : ComponentActivity() {

    private val leaderboardViewModel: LeaderboardViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuizzTheme {
                val users by leaderboardViewModel.users.collectAsState()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Leaderboard") },
                            actions = {
                                TextButton(onClick = {
                                    leaderboardViewModel.logout()
                                    startActivity(Intent(this@LeaderboardActivity, HomeActivity::class.java))
                                    finish()
                                }) {
                                    Text("Disconnect")
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
                                LeaderboardItem(
                                    rank = index + 1,
                                    user = user,
                                    isTopUser = index == 0
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(rank: Int, user: UserEntity, isTopUser: Boolean) {
    val rainbowColors = listOf(
        Color.Red, Color.Magenta,
        Color.Blue, Color.Green, Color.Yellow, Color(0xFFFFA500)
    )

    val transition = rememberInfiniteTransition(label = "RainbowBlink")

    val colorIndex by transition.animateValue(
        initialValue = 0,
        targetValue = rainbowColors.size - 1,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RainbowIndex"
    )

    val backgroundColor = if (isTopUser) {
        rainbowColors[colorIndex % rainbowColors.size]
    } else {
        Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$rank. ${user.firstname} ${user.lastname}")
            Text("Score: ${user.score}")
        }
    }
}

