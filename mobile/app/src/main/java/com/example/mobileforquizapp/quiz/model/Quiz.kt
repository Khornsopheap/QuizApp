package com.example.mobileforquizapp.quiz.model

data class Quiz(
    val id: Long? = null,
    val title: String,
    val description: String,
    val questions: List<Question> = emptyList(),
    val questionCount: Int? = null


)
