package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var createQuizButton: MaterialButton
    private lateinit var navHome: LinearLayout
    private lateinit var navQuizzes: LinearLayout
    private lateinit var navLeagues: LinearLayout
    private lateinit var navProfile: LinearLayout
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        recyclerView     = findViewById(R.id.adminQuizRecyclerView)
        createQuizButton = findViewById(R.id.createQuizButton)
        navHome          = findViewById(R.id.navHome)
        navQuizzes       = findViewById(R.id.navQuizzes)
        navLeagues       = findViewById(R.id.navLeagues)
        navProfile       = findViewById(R.id.navProfile)

        recyclerView.layoutManager = LinearLayoutManager(this)

        token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)
            ?: intent.getStringExtra("jwt_token")

        createQuizButton.setOnClickListener {
            startActivity(
                Intent(this, CreateQuizActivity::class.java).apply {
                    putExtra("jwt_token", token)
                }
            )
        }

        // Set Home as default selected
        setSelected(navHome)

        navHome.setOnClickListener {
            setSelected(navHome)
        }

        navQuizzes.setOnClickListener {
            setSelected(navQuizzes)
            startActivity(
                Intent(this, QuizManagementActivity::class.java).apply {
                    putExtra("jwt_token", token)
                }
            )
        }

        navLeagues.setOnClickListener {
            setSelected(navLeagues)
            startActivity(
                Intent(this, LeaderboardActivity::class.java).apply {
                    putExtra("jwt_token", token)
                }
            )
        }

        navProfile.setOnClickListener {
            setSelected(navProfile)
            startActivity(
                Intent(this, LeaderboardActivity::class.java).apply {
                    putExtra("jwt_token", token)
                }
            )        }

        loadQuizzes()
    }

    private fun setSelected(selected: LinearLayout) {
        val allNavs = listOf(navHome, navQuizzes, navLeagues, navProfile)
        val activeColor   = Color.parseColor("#630ed4")
        val inactiveColor = Color.parseColor("#94a3b8")

        allNavs.forEach { nav ->
            val icon  = nav.getChildAt(0) as ImageView
            val label = nav.getChildAt(1) as TextView

            if (nav == selected) {
                nav.setBackgroundResource(R.drawable.nav_selected_bg)
                icon.imageTintList = ColorStateList.valueOf(activeColor)
                label.setTextColor(activeColor)
                label.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            } else {
                nav.background = null
                icon.imageTintList = ColorStateList.valueOf(inactiveColor)
                label.setTextColor(inactiveColor)
                label.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setSelected(navHome)
        loadQuizzes()
    }

    private fun loadQuizzes() {
        RetrofitClient.apiService.getQuizzes("Bearer $token")
            .enqueue(object : Callback<List<Quiz>> {
                override fun onResponse(call: Call<List<Quiz>>, response: Response<List<Quiz>>) {
                    if (response.isSuccessful) {
                        val quizzes = response.body() ?: emptyList()
                        findViewById<TextView>(R.id.totalQuizzesText).text = quizzes.size.toString()
                        recyclerView.adapter = QuizListAdapter(
                            quizzes,
                            isAdmin = true,
                            onQuizClick = { quiz ->
                                startActivity(
                                    Intent(
                                        this@AdminDashboardActivity,
                                        AdminQuizDetailActivity::class.java
                                    ).apply {
                                        putExtra("quiz_id", quiz.id)
                                        putExtra("jwt_token", token)
                                    }
                                )
                            }
                        )
                    } else {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            "Failed (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}