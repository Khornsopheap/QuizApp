package com.example.mobileforquizapp.login.model

data class RegisterRequest(
    val username: String,
    val password: String,
    val role: String = "USER"
)
