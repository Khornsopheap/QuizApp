package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDashboardActivity : UserBaseActivity() {
    override fun currentNavItem() = NAV_HOME

    private lateinit var recyclerView: RecyclerView
    private lateinit var joinGameButton: ExtendedFloatingActionButton
    private lateinit var tvWelcome: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)
        setupNav()

        recyclerView   = findViewById(R.id.userQuizRecyclerView)
        joinGameButton = findViewById(R.id.joinGameButton)
        tvWelcome      = findViewById(R.id.tvWelcome)

        val logoutBtn = findViewById<MaterialCardView>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            getSharedPreferences("MyApp", MODE_PRIVATE).edit().clear().apply()
            val intent = Intent(this, com.example.mobileforquizapp.login.LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

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

                        recyclerView.adapter = QuizListAdapter(
                            quizzes,
                            isAdmin = false,
                            onQuizClick = { quiz ->
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
