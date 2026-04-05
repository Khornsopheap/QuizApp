package com.example.mobileforquizapp.login.model

data class LoginResponse(
    val token: String,
    val message: String,
    val expireIn: String
)
