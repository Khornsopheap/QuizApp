package com.example.mobileforquizapp.quiz.model

data class Question(
    val id: Long? = null,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val score: Int
)

