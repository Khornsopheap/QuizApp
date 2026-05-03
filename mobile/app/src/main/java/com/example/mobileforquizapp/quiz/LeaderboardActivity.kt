package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var backBtn: ImageView
    private lateinit var adapter: LeaderboardAdapter

    // Podium views
    private lateinit var nameFirst: TextView
    private lateinit var nameSecond: TextView
    private lateinit var nameThird: TextView
    private lateinit var scoreFirst: TextView
    private lateinit var scoreSecond: TextView
    private lateinit var scoreThird: TextView

    private var roomCode: String = ""
    private var token: String? = null
    private var myScore: Int = 0

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

        backBtn      = findViewById(R.id.backBtn)
        recyclerView = findViewById(R.id.leaderboardRecyclerView)

        // Podium views
        nameFirst   = findViewById(R.id.nameFirst)
        nameSecond  = findViewById(R.id.nameSecond)
        nameThird   = findViewById(R.id.nameThird)
        scoreFirst  = findViewById(R.id.scoreFirst)
        scoreSecond = findViewById(R.id.scoreSecond)
        scoreThird  = findViewById(R.id.scoreThird)

        roomCode = intent.getStringExtra("room_code") ?: ""
        token    = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)
        myScore  = intent.getIntExtra("my_score", 0)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LeaderboardAdapter(emptyList())
        recyclerView.adapter = adapter

        backBtn.setOnClickListener { finish() }

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
                        bindPodium(entries)

                        // Pass rank 4+ to the RecyclerView
                        val remaining = if (entries.size > 3) entries.subList(3, entries.size) else emptyList()
                        adapter = LeaderboardAdapter(remaining)
                        recyclerView.adapter = adapter
                    }
                }

                override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                    // Silently fail — will retry in 5s
                }
            })
    }

    private fun bindPodium(list: List<Map<String, Any>>) {
        fun getName(map: Map<String, Any>): String =
            (map["username"] as? String) ?: (map["name"] as? String) ?: "—"

        fun getScore(map: Map<String, Any>): String {
            val score = map["score"]
            return when (score) {
                is Double -> score.toInt().toString() + " pts"
                is Int    -> "$score pts"
                is String -> "$score pts"
                else      -> "0 pts"
            }
        }

        // 1st place
        if (list.isNotEmpty()) {
            nameFirst.text  = getName(list[0])
            scoreFirst.text = getScore(list[0])
        } else {
            nameFirst.text  = "—"
            scoreFirst.text = ""
        }

        // 2nd place
        if (list.size > 1) {
            nameSecond.text  = getName(list[1])
            scoreSecond.text = getScore(list[1])
        } else {
            nameSecond.text  = "—"
            scoreSecond.text = ""
        }

        // 3rd place
        if (list.size > 2) {
            nameThird.text  = getName(list[2])
            scoreThird.text = getScore(list[2])
        } else {
            nameThird.text  = "—"
            scoreThird.text = ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(pollRunnable)
    }
}