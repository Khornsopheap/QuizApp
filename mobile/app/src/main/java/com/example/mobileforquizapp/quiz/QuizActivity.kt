package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.example.mobileforquizapp.quiz.model.QuizSubmission
import com.example.mobileforquizapp.quiz.model.ResultResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private val answersMap = mutableMapOf<Long, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        viewPager = findViewById(R.id.quizViewPager)

        val prefs = getSharedPreferences("MyApp", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            Log.e("QuizActivity", "No token found in SharedPreferences")
            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("QuizActivity", "Token being sent: Bearer $token")

        // Fetch quizzes from backend
        RetrofitClient.apiService.getQuizzes("Bearer $token")
            .enqueue(object : Callback<List<Quiz>> {
                override fun onResponse(call: Call<List<Quiz>>, response: Response<List<Quiz>>) {
                    Log.d("QuizActivity", "Response code: ${response.code()}")

                    if (response.isSuccessful) {
                        val quizzes = response.body() ?: emptyList()
                        Log.d("QuizActivity", "Quizzes received: $quizzes")

                        if (quizzes.isNotEmpty()) {
                            viewPager.adapter = QuizPagerAdapter(this@QuizActivity, quizzes)
                        } else {
                            Toast.makeText(this@QuizActivity, "No quizzes available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("QuizActivity", "Error body: ${response.errorBody()?.string()}")
                        Toast.makeText(this@QuizActivity, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {
                    Log.e("QuizActivity", "Network error", t)
                    Toast.makeText(this@QuizActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })

        // Submit button
        findViewById<com.google.android.material.button.MaterialButton>(R.id.submitButton).setOnClickListener {
            submitAnswers(token)
        }
    }

    fun saveAnswer(quizId: Long, answer: String) {
        answersMap[quizId] = answer
        Log.d("QuizActivity", "Answer saved: $quizId -> $answer")
    }

    fun goToNextQuestion() {
        if (viewPager.currentItem < viewPager.adapter!!.itemCount - 1) {
            viewPager.currentItem += 1
        } else {
            val token = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .getString("jwt_token", "") ?: ""
            submitAnswers(token)
        }
    }

    private fun submitAnswers(token: String) {
        val submission = QuizSubmission(answersMap)

        // Log the token and submission
        Log.d("QuizActivity", "Submitting with token: Bearer $token")
        Log.d("QuizActivity", "Submitting answers object: $submission")

        // Convert to JSON for clarity
        val submissionJson = com.google.gson.Gson().toJson(submission)
        Log.d("QuizActivity", "Submitting answers JSON: $submissionJson")

        RetrofitClient.apiService.submitQuiz("Bearer $token", submission)
            .enqueue(object : Callback<ResultResponse> {
                override fun onResponse(call: Call<ResultResponse>, response: Response<ResultResponse>) {
                    Log.d("QuizActivity", "Submit response code: ${response.code()}")

                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("QuizActivity", "Submit success: $result")
                        Toast.makeText(
                            this@QuizActivity,
                            "Score: ${result?.score}/${result?.total}\n${result?.feedback}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("QuizActivity", "Submit error body: $errorBody")
                        Toast.makeText(
                            this@QuizActivity,
                            "Submit failed: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
                    Log.e("QuizActivity", "Submit network error: ${t.message}", t)
                    Toast.makeText(
                        this@QuizActivity,
                        "Error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}
