package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.example.mobileforquizapp.quiz.model.QuizSubmission
import com.example.mobileforquizapp.quiz.model.ResultResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class QuizActivity : AppCompatActivity() {
    private lateinit var quizAdapter: QuizAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val recyclerView = findViewById<RecyclerView>(R.id.quizRecyclerView)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val addQuizButton = findViewById<Button>(R.id.addQuiz)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val token = getSharedPreferences("MyApp", MODE_PRIVATE)
            .getString("jwt_token", "") ?: ""
        Log.d("QuizActivity", "Token read from SharedPreferences: $token")

        // Fetch quizzes
        RetrofitClient.apiService.getQuizzes("Bearer $token")
            .enqueue(object : Callback<List<Quiz>> {
                override fun onResponse(call: Call<List<Quiz>>, response: Response<List<Quiz>>) {
                    if (response.isSuccessful) {
                        val quizzes = response.body() ?: emptyList()
                        Log.d("QuizActivity", "Fetched quizzes: $quizzes")

                        quizAdapter = QuizAdapter(quizzes)
                        recyclerView.adapter = quizAdapter

                        // Handle submit button click
                        submitButton.setOnClickListener {
                            val answersMap = quizAdapter.getSelectedAnswersMap()
                            submitAnswers(answersMap)
                        }
                        addQuizButton.setOnClickListener {
                            val intent = Intent(this@QuizActivity, AdminAddQuizActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        Log.e("QuizActivity", "Failed: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {
                    Log.e("QuizActivity", "Error fetching quizzes", t)
                }
            })
    }

    // Submit answers as Map<Long,String>
    private fun submitAnswers(userAnswers: Map<Long, String>) {
        val token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", "") ?: ""
        val submission = QuizSubmission(
            quizId = 1L, // or whichever quiz session ID
            answers = userAnswers
        )

        RetrofitClient.apiService.submitQuiz("Bearer $token", submission)
            .enqueue(object : Callback<ResultResponse> {
                override fun onResponse(call: Call<ResultResponse>, response: Response<ResultResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("QuizActivity", "Score: ${result?.score}, Feedback: ${result?.feedback}")
                    } else {
                        Log.e("QuizActivity", "Submit error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
                    Log.e("QuizActivity", "Error submitting quiz", t)
                }
            })
    }

}
