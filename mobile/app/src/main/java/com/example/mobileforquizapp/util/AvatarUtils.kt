package com.example.mobileforquizapp.util

object AvatarUtils {
    fun getAvatarUrl(username: String): String {
        val seed = username.trim().lowercase().replace(" ", "")
        return "https://api.dicebear.com/7.x/personas/png?seed=$seed&size=128"
    }
}