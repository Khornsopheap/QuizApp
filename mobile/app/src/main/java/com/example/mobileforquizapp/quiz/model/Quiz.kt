package com.example.mobileforquizapp.quiz.model

data class Quiz(
    val id: Long? = null,
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

