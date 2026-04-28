package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
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

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var joinGameButton: MaterialButton
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)
            ?: intent.getStringExtra("jwt_token")

        recyclerView  = findViewById(R.id.userQuizRecyclerView)
        joinGameButton = findViewById(R.id.joinGameButton)
        findViewById<MaterialButton>(R.id.logoutBtn).setOnClickListener {
            getSharedPreferences("MyApp", MODE_PRIVATE).edit().clear().apply()
            val intent = android.content.Intent(this, com.example.mobileforquizapp.login.LoginActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Join Game button opens JoinRoomActivity
        joinGameButton.setOnClickListener {
            val intent = Intent(this, JoinRoomActivity::class.java)
            intent.putExtra("jwt_token", token)
            startActivity(intent)
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

                        // ✅ isAdmin = false fixes the error
                        recyclerView.adapter = QuizListAdapter(
                            quizzes,
                            isAdmin = false,
                            onQuizClick = { quiz ->
                                // Users just see quiz info — they join via room code
                                Toast.makeText(
                                    this@UserDashboardActivity,
                                    "Use Join Game to play a live quiz!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    } else {
                        Toast.makeText(
                            this@UserDashboardActivity,
                            "Failed to load quizzes (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {
                    Toast.makeText(
                        this@UserDashboardActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}