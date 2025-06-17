package com.supdevinci.quizz.view

import android.os.Bundle
import android.text.Html
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supdevinci.quizz.R
import com.supdevinci.quizz.model.UserEntity
import com.supdevinci.quizz.ui.theme.QuizzTheme
import com.supdevinci.quizz.viewmodel.MainViewModel
import com.supdevinci.quizz.viewmodel.QuizzViewModel

class MainActivity : ComponentActivity() {

    private val quizzViewModel: QuizzViewModel by viewModels()
    private val userViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuizzTheme {
                val user by userViewModel.user.collectAsState()

                if (user == null) {
                    var firstname by remember { mutableStateOf("") }
                    var lastname by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Bienvenue !", style = typography.titleLarge)
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = firstname,
                            onValueChange = { firstname = it },
                            label = { Text("Prénom") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = lastname,
                            onValueChange = { lastname = it },
                            label = { Text("Nom") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (firstname.isNotBlank() && lastname.isNotBlank()) {
                                    userViewModel.saveUser(firstname, lastname)
                                }
                            }
                        ) {
                            Text("Commencer")
                        }
                    }
                } else {
                    QuizContent(user = user!!, quizzViewModel = quizzViewModel, userViewModel = userViewModel)
                }
            }
        }
    }
}

@Composable
fun QuizContent(user: UserEntity, quizzViewModel: QuizzViewModel, userViewModel: MainViewModel) {
    val question = quizzViewModel.question.collectAsState()
    val error = quizzViewModel.error.collectAsState()
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(quizzViewModel.tokenReady.collectAsState().value) {
        if (quizzViewModel.tokenReady.value) {
            quizzViewModel.fetchQuestion()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorSecondary))
            .padding(16.dp)
    ) {
        Text("Nom : ${user.firstname} ${user.lastname}", style = typography.titleMedium)
        Text("Score : ${user.score}", style = typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        error.value?.let {
            Text("Erreur : $it", color = MaterialTheme.colorScheme.error)
        }

        question.value?.let { q ->
            val shuffledAnswers = remember(q.question) {
                q.getShuffledAnswers()
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Catégorie : ${q.category}", style = typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = Html.fromHtml(q.question).toString(),
                        style = typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(12.dp))

                    shuffledAnswers.forEach { answer ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selectedAnswer == answer,
                                onCheckedChange = {
                                    selectedAnswer = if (it) answer else null
                                    showResult = true
                                    if (it && answer == q.correct_answer) {
                                        userViewModel.incrementScore()
                                    }
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(Html.fromHtml(answer).toString())
                        }
                    }

                    if (showResult && selectedAnswer != null) {
                        val isCorrect = selectedAnswer == q.correct_answer
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrect)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text(
                                text = if (isCorrect) "Bonne réponse !" else "Mauvaise réponse !",
                                modifier = Modifier.padding(12.dp),
                                style = typography.bodyLarge
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            selectedAnswer = null
                            showResult = false
                            quizzViewModel.fetchQuestion()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Question suivante")
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            // Aller à la 2e activité
        }) {
            Text("Aller à la deuxième activité")
        }
    }
}
