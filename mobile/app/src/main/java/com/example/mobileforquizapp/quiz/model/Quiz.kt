package com.example.mobileforquizapp.quiz.model

data class Quiz(
    val id: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val questions: List<Question> = emptyList(),
    val questionCount: Int? = null
)