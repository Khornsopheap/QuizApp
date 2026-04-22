package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
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
        val quizId = intent.getLongExtra("quiz_id", -1)

        if (token.isNullOrEmpty() || quizId == -1L) {
            Toast.makeText(this, "Invalid session, please log in again", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.apiService.getQuestionsByQuizId("Bearer $token", quizId)
            .enqueue(object : Callback<List<Question>> {
                override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                    if (response.isSuccessful) {
                        val questions = response.body() ?: emptyList()
                        if (questions.isNotEmpty()) {
                            viewPager.adapter = QuizPagerAdapter(this@QuizActivity, questions)
                        } else {
                            Toast.makeText(this@QuizActivity, "No questions available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@QuizActivity, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                    Toast.makeText(this@QuizActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })

        findViewById<com.google.android.material.button.MaterialButton>(R.id.submitButton)
            .setOnClickListener { submitAnswers(token) }
    }

    fun saveAnswer(questionId: Long, answer: String) {
        answersMap[questionId] = answer
    }

    fun goToNextQuestion() {
        if (viewPager.currentItem < viewPager.adapter!!.itemCount - 1) {
            viewPager.currentItem += 1
        } else {
            val token = getSharedPreferences("MyApp", MODE_PRIVATE)
                .getString("jwt_token", "") ?: ""
            submitAnswers(token)
        }
    }

    private fun submitAnswers(token: String) {
        val quizId = intent.getLongExtra("quiz_id", -1)
        val submission = QuizSubmission(quizId, answersMap)
        RetrofitClient.apiService.submitQuiz("Bearer $token", submission)
            .enqueue(object : Callback<ResultResponse> {
                override fun onResponse(call: Call<ResultResponse>, response: Response<ResultResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Toast.makeText(
                            this@QuizActivity,
                            "Score: ${result?.score}/${result?.total}\n${result?.feedback}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this@QuizActivity, "Submit failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
                    Toast.makeText(this@QuizActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

