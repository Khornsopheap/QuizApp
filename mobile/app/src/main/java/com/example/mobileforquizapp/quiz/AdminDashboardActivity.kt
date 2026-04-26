package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.example.mobileforquizapp.quiz.model.Quiz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)
            ?: intent.getStringExtra("jwt_token")

        recyclerView = findViewById(R.id.adminQuizRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create quiz button
        findViewById<ExtendedFloatingActionButton>(R.id.createQuizButton).setOnClickListener {
            startActivity(Intent(this, CreateQuizActivity::class.java).apply {
                putExtra("jwt_token", token)
            })
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

                        // ✅ isAdmin = true fixes the error
                        recyclerView.adapter = QuizListAdapter(
                            quizzes,
                            isAdmin = true,
                            onQuizClick = { quiz ->
                                val intent = Intent(
                                    this@AdminDashboardActivity,
                                    AdminQuizDetailActivity::class.java
                                )
                                intent.putExtra("quiz_id", quiz.id)
                                intent.putExtra("jwt_token", token)
                                startActivity(intent)
                            }
                        )
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
}