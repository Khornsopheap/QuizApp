package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.login.LoginActivity
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizListActivity : AppCompatActivity() {

    private lateinit var fabAddQuiz: FloatingActionButton

    private var token: String? = null
    private var isAdmin: Boolean = false
    private val quizzes = mutableListOf<Quiz>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizzes)

        fabAddQuiz = findViewById(R.id.fabAddQuiz)

        token = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)
        isAdmin = intent.getBooleanExtra("is_admin", false).let {
            if (!it) getSharedPreferences("MyApp", MODE_PRIVATE).getBoolean("is_admin", false)
            else it
        }

        if (token == null) {
            goToLogin()
            return
        }

        fabAddQuiz.visibility = if (isAdmin) android.view.View.VISIBLE else android.view.View.GONE

        fabAddQuiz.setOnClickListener {
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
                        quizzes.clear()
                        quizzes.addAll(response.body() ?: emptyList())
                        // NOTE: The new activity_quizzes.xml uses item_quiz_management includes
                        // for a preview layout. For dynamic quiz loading, add a RecyclerView
                        // to activity_quizzes.xml and bind an adapter here.
                    } else if (response.code() == 403) {
                        Toast.makeText(
                            this@QuizListActivity,
                            "Session expired. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        goToLogin()
                    } else {
                        Toast.makeText(
                            this@QuizListActivity,
                            "Failed to load quizzes (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {
                    Toast.makeText(
                        this@QuizListActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
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
