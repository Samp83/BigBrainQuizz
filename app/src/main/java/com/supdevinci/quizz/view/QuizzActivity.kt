package com.supdevinci.quizz.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supdevinci.quizz.model.User
import com.supdevinci.quizz.ui.theme.QuizzTheme
import com.supdevinci.quizz.viewmodel.QuizzViewModel

class QuizzActivity : ComponentActivity() {

    private val quizzViewModel: QuizzViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryId = intent.getIntExtra("categoryId", -1)
        val categoryName = intent.getStringExtra("categoryName") ?: "Unknown"

        if (categoryId != -1) {
            quizzViewModel.initialize(categoryId)
        }

        setContent {
            QuizzTheme {
                val user by quizzViewModel.currentUser.collectAsState()
                val question by quizzViewModel.question.collectAsState()
                val error by quizzViewModel.error.collectAsState()
                val tokenReady by quizzViewModel.tokenReady.collectAsState()

                val context = LocalContext.current

                var selectedAnswer by remember { mutableStateOf<String?>(null) }
                var isAnswerSubmitted by remember { mutableStateOf(false) }

                LaunchedEffect(tokenReady) {
                    if (tokenReady) quizzViewModel.fetchQuestion()
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFEFEFEF))
                        .padding(16.dp)
                ) {
                    Text("Category : $categoryName", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))

                    user?.let {
                        Text("Player : ${it.firstname} ${it.lastname}")
                        Text("Score : ${it.score}")
                    }

                    Spacer(Modifier.height(16.dp))

                    error?.let {
                        Text("Error : $it", color = MaterialTheme.colorScheme.error)
                    }

                    AnimatedContent(
                        targetState = question,
                        transitionSpec = {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        },
                        label = "QuestionTransition"
                    ) { q ->
                        q?.let {
                            val answers = remember(q.question) { q.getShuffledAnswers() }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        q.question,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(Modifier.height(12.dp))

                                    answers.forEach { answer ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .selectable(
                                                    selected = selectedAnswer == answer,
                                                    enabled = !isAnswerSubmitted,
                                                    onClick = {
                                                        selectedAnswer = answer
                                                        isAnswerSubmitted = true
                                                        if (answer == q.correct_answer) {
                                                            user?.let { quizzViewModel.incrementScore(it) }
                                                        }
                                                    }
                                                )
                                                .padding(vertical = 4.dp)
                                        ) {
                                            RadioButton(
                                                selected = selectedAnswer == answer,
                                                onClick = null,
                                                enabled = !isAnswerSubmitted
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(answer)
                                        }
                                    }

                                    if (isAnswerSubmitted) {
                                        val correct = selectedAnswer == q.correct_answer
                                        Spacer(Modifier.height(12.dp))
                                        Text(
                                            if (correct) "Correct answer !" else "Wrong answer !",
                                            color = if (correct) Color.Green else Color.Red,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            selectedAnswer = null
                                            isAnswerSubmitted = false
                                            quizzViewModel.fetchQuestion()
                                        },
                                        modifier = Modifier.align(Alignment.End),
                                        enabled = isAnswerSubmitted
                                    ) {
                                        Text("Next question")
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            context.startActivity(Intent(context, LeaderboardActivity::class.java))
                                            (context as? ComponentActivity)?.finish()
                                        },
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                    ) {
                                        Text("Finish and show leaderboard")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
