package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var myScoreText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var backHomeButton: MaterialButton
    private lateinit var adapter: LeaderboardAdapter

    private var roomCode: String = ""
    private var token: String? = null
    private var myScore: Int = 0

    // ✅ Poll every 5 seconds so leaderboard updates as others finish
    private val handler = Handler(Looper.getMainLooper())
    private val pollRunnable = object : Runnable {
        override fun run() {
            fetchLeaderboard()
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        myScoreText    = findViewById(R.id.myScoreText)
        recyclerView   = findViewById(R.id.leaderboardRecyclerView)
        backHomeButton = findViewById(R.id.backHomeButton)

        roomCode = intent.getStringExtra("room_code") ?: ""
        token    = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)
        myScore  = intent.getIntExtra("my_score", 0)

        myScoreText.text = "Your score: $myScore pts"

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LeaderboardAdapter(emptyList())
        recyclerView.adapter = adapter

        backHomeButton.setOnClickListener {
            finish()
        }

        // Start polling
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
                        val entries = response.body() ?: emptyList()
                        // ✅ Rebuild adapter with fresh data
                        recyclerView.adapter = LeaderboardAdapter(entries)
                    }
                }
                override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                    // silently fail — will retry in 5s
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(pollRunnable) // ✅ stop polling
    }
}