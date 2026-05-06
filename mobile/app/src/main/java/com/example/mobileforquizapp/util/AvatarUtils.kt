package com.example.mobileforquizapp.util

object AvatarUtils {
    fun getAvatarUrl(username: String): String {
        val hash  = username.trim().lowercase().hashCode().and(0x7FFFFFFF)
        val index = (hash % 99) + 1
        // Alternate between men and women based on hash
        val gender = if (hash % 2 == 0) "women" else "men"
        return "https://randomuser.me/api/portraits/$gender/$index.jpg"
    }
}