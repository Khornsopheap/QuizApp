package com.example.mobileforquizapp.network

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    fun getUsername(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING))
            val json = JSONObject(payload)
            json.optString("sub").ifEmpty { json.optString("username") }.ifEmpty { null }
        } catch (e: Exception) { null }
    }
}