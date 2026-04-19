package com.example.mobileforquizapp.util

import android.util.Base64
import org.json.JSONObject

object AuthUtils {
    fun getRoleFromToken(token: String): String {
        val parts = token.split(".")
        if (parts.size == 3) {
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            return json.getString("role")
        }
        return ""
    }
}