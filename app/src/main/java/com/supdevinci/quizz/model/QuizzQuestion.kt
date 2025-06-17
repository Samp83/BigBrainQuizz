package com.supdevinci.quizz.model

data class QuizzQuestion(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
) {
    fun getShuffledAnswers(): List<String> {
        return (incorrect_answers + correct_answer).shuffled()
    }
}
