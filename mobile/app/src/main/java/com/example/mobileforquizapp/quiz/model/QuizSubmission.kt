package com.example.mobileforquizapp.quiz.model

data class QuizSubmission(
    val quizId: Long,
    val answers: Map<Long, String>
)
