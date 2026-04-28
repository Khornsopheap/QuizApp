package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.login.LoginActivity
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var createQuizButton: MaterialButton
    private lateinit var logoutBtn: MaterialButton
    private lateinit var tvTotalQuizzes: TextView
    private lateinit var tvTotalQuestions: TextView

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Bind views
        recyclerView      = findViewById(R.id.adminQuizRecyclerView)
        bottomNav         = findViewById(R.id.bottomNavigationView)
        createQuizButton  = findViewById(R.id.createQuizButton)
        logoutBtn         = findViewById(R.id.logoutBtn)
        tvTotalQuizzes    = findViewById(R.id.tvTotalQuizzes)
        tvTotalQuestions  = findViewById(R.id.tvTotalQuestions)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get token
        token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)
            ?: intent.getStringExtra("jwt_token")

        if (token == null) {
            goToLogin()
            return
        }

        // Logout
        logoutBtn.setOnClickListener { logout() }

        // Create quiz button
        createQuizButton.setOnClickListener {
            startActivity(
                Intent(this, CreateQuizActivity::class.java).apply {
                    putExtra("jwt_token", token)
                }
            )
        }

        // Bottom nav
        bottomNav.selectedItemId = R.id.nav_dashboard
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true // already here
                R.id.nav_quizzes -> {
                    startActivity(
                        Intent(this, QuizListActivity::class.java).apply {
                            putExtra("jwt_token", token)
                            putExtra("is_admin", true)
                        }
                    )
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        loadQuizzes()
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.nav_dashboard
        loadQuizzes()
    }

    private fun loadQuizzes() {
        RetrofitClient.apiService.getQuizzes("Bearer $token")
            .enqueue(object : Callback<List<Quiz>> {
                override fun onResponse(
                    call: Call<List<Quiz>>,
                    response: Response<List<Quiz>>
                ) {
                    if (response.isSuccessful) {
                        val quizzes = response.body() ?: emptyList()

                        // Update stat counters
                        tvTotalQuizzes.text = quizzes.size.toString()
                        tvTotalQuestions.text = quizzes
                            .sumOf { it.questionCount ?: 0 }
                            .toString()

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
                    } else if (response.code() == 403) {
                        goToLogin()
                    } else {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            "Failed to load quizzes (${response.code()})",
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

    private fun logout() {
        getSharedPreferences("MyApp", MODE_PRIVATE).edit().clear().apply()
        goToLogin()
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }
}