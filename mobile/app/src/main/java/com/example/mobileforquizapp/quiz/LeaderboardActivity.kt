package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.util.AvatarUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaderboardActivity : BaseActivity() {

    override fun currentNavItem() = NAV_LEAGUES

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaderboardAdapter
    private lateinit var nameFirst: TextView
    private lateinit var nameSecond: TextView
    private lateinit var nameThird: TextView
    private lateinit var scoreFirst: TextView
    private lateinit var scoreSecond: TextView
    private lateinit var scoreThird: TextView
    private lateinit var avatarFirst: ImageView
    private lateinit var avatarSecond: ImageView
    private lateinit var avatarThird: ImageView

    private var roomCode: String = ""
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

        setupNav()

        recyclerView  = findViewById(R.id.leaderboardRecyclerView)
        nameFirst     = findViewById(R.id.nameFirst)
        nameSecond    = findViewById(R.id.nameSecond)
        nameThird     = findViewById(R.id.nameThird)
        scoreFirst    = findViewById(R.id.scoreFirst)
        scoreSecond   = findViewById(R.id.scoreSecond)
        scoreThird    = findViewById(R.id.scoreThird)
        avatarFirst   = findViewById(R.id.avatarFirst)
        avatarSecond  = findViewById(R.id.avatarSecond)
        avatarThird   = findViewById(R.id.avatarThird)

        findViewById<ImageView>(R.id.backBtn)?.setOnClickListener { finish() }

        roomCode = intent.getStringExtra("room_code") ?: ""
        myScore  = intent.getIntExtra("my_score", 0)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LeaderboardAdapter(emptyList())
        recyclerView.adapter = adapter

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
                        val remaining = if (entries.size > 3) {
                            entries.subList(3, entries.size)
                        } else {
                            emptyList()
                        }
                        adapter = LeaderboardAdapter(remaining)
                        recyclerView.adapter = adapter
                    }
                }

                override fun onFailure(
                    call: Call<List<Map<String, Any>>>,
                    t: Throwable
                ) {
                    // Silently retry in 5s
                }
            })
    }

    private fun bindPodium(list: List<Map<String, Any>>) {
        fun getName(map: Map<String, Any>) =
            (map["username"] as? String) ?: (map["name"] as? String) ?: "—"

        fun getScore(map: Map<String, Any>): String {
            return when (val s = map["score"]) {
                is Double -> "${s.toInt()} pts"
                is Int    -> "$s pts"
                is String -> "$s pts"
                else      -> "0 pts"
            }
        }

        fun loadAvatar(imageView: ImageView, name: String) {
            Glide.with(this)
                .load(AvatarUtils.getAvatarUrl(name))
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(imageView)
        }

        // 1st place
        if (list.isNotEmpty()) {
            val name = getName(list[0])
            nameFirst.text  = name
            scoreFirst.text = getScore(list[0])
            loadAvatar(avatarFirst, name)
        } else {
            nameFirst.text  = "—"
            scoreFirst.text = ""
        }

        // 2nd place
        if (list.size > 1) {
            val name = getName(list[1])
            nameSecond.text  = name
            scoreSecond.text = getScore(list[1])
            loadAvatar(avatarSecond, name)
        } else {
            nameSecond.text  = "—"
            scoreSecond.text = ""
        }

        // 3rd place
        if (list.size > 2) {
            val name = getName(list[2])
            nameThird.text  = name
            scoreThird.text = getScore(list[2])
            loadAvatar(avatarThird, name)
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