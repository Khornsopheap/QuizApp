package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomCodeActivity : AppCompatActivity() {

    private lateinit var roomCodeText: TextView
    private lateinit var playersJoinedText: TextView
    private lateinit var viewLeaderboardButton: MaterialButton

    private var roomCode: String = ""
    private var quizId: Long = -1
    private var token: String? = null

    // Poll every 3 seconds to update player count
    private val handler = Handler(Looper.getMainLooper())
    private val pollRunnable = object : Runnable {
        override fun run() {
            fetchLeaderboard()
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_code)

        roomCodeText         = findViewById(R.id.roomCodeText)
        playersJoinedText    = findViewById(R.id.playersJoinedText)
        viewLeaderboardButton = findViewById(R.id.viewLeaderboardButton)

        roomCode = intent.getStringExtra("room_code") ?: ""
        quizId   = intent.getLongExtra("quiz_id", -1)
        token    = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)

        roomCodeText.text = roomCode

        viewLeaderboardButton.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            intent.putExtra("room_code", roomCode)
            intent.putExtra("jwt_token", token)
            startActivity(intent)
        }

        // Start polling for player count
        handler.post(pollRunnable)
    }

    private fun fetchLeaderboard() {
        RetrofitClient.apiService.getLeaderboard("Bearer $token", roomCode)
            .enqueue(object : Callback<List<Map<String, Any>>> {
                override fun onResponse(
                    call: Call<List<Map<String, Any>>>,
                    response: Response<List<Map<String, Any>>>
                ) {
                    if (response.isSuccessful) {
                        val count = response.body()?.size ?: 0
                        playersJoinedText.text = "👥 $count players joined"
                    }
                }
                override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {}
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(pollRunnable) // ✅ stop polling when screen closes
    }
}