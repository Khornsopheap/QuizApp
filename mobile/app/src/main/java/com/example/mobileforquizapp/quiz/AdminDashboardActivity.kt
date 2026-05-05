package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : BaseActivity() {

    override fun currentNavItem() = NAV_HOME

    private lateinit var recyclerView: RecyclerView
    private lateinit var createQuizButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        setupNav()

        recyclerView     = findViewById(R.id.adminQuizRecyclerView)
        createQuizButton = findViewById(R.id.createQuizButton)
        recyclerView.layoutManager = LinearLayoutManager(this)

        createQuizButton.setOnClickListener {
            startActivity(
                Intent(this, CreateQuizActivity::class.java).apply {
                    putExtra("jwt_token", token)
                }
            )
        }

        loadQuizzes()
    }

    override fun onResume() {
        super.onResume()
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

                        // Update stat card
                        findViewById<TextView>(R.id.totalQuizzesText).text =
                            quizzes.size.toString()

                        // Show only first 5 on dashboard
                        val preview = if (quizzes.size > 5) quizzes.subList(0, 5) else quizzes

                        recyclerView.adapter = QuizListAdapter(
                            quizzes   = preview,
                            isAdmin   = true,
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