package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveQuizActivity : AppCompatActivity() {

    private lateinit var questionCountText: TextView
    private lateinit var scoreText: TextView
    private lateinit var questionProgress: ProgressBar
    private lateinit var questionText: TextView
    private lateinit var option1Button: MaterialButton
    private lateinit var option2Button: MaterialButton
    private lateinit var option3Button: MaterialButton
    private lateinit var option4Button: MaterialButton
    private lateinit var nextButton: MaterialButton

    private var questions = listOf<Question>()
    private var currentIndex = 0
    private var totalScore = 0
    private var selectedAnswer: String? = null

    private var roomCode: String = ""
    private var quizId: Long = -1
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_quiz)

        questionCountText = findViewById(R.id.questionCountText)
        scoreText         = findViewById(R.id.scoreText)
        questionProgress  = findViewById(R.id.questionProgress)
        questionText      = findViewById(R.id.questionText)
        option1Button     = findViewById(R.id.option1Button)
        option2Button     = findViewById(R.id.option2Button)
        option3Button     = findViewById(R.id.option3Button)
        option4Button     = findViewById(R.id.option4Button)
        nextButton        = findViewById(R.id.nextButton)

        roomCode = intent.getStringExtra("room_code") ?: ""
        quizId   = intent.getLongExtra("quiz_id", -1)
        token    = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)

        if (token == null || quizId == -1L) {
            Toast.makeText(this, "Invalid session.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Option click listeners
        listOf(option1Button, option2Button, option3Button, option4Button).forEach { btn ->
            btn.setOnClickListener { onOptionSelected(btn) }
        }

        nextButton.setOnClickListener {
            val current = questions[currentIndex]
            // ✅ Check answer and add score
            if (selectedAnswer == current.correctAnswer) {
                totalScore += current.score
            }
            scoreText.text = "⭐ $totalScore pts"

            if (currentIndex < questions.size - 1) {
                currentIndex++
                showQuestion()
            } else {
                // ✅ All questions done — submit score
                submitScore()
            }
        }

        loadQuestions()
    }

    private fun loadQuestions() {
        RetrofitClient.apiService.getQuestionsByQuizId("Bearer $token", quizId)
            .enqueue(object : Callback<List<Question>> {
                override fun onResponse(
                    call: Call<List<Question>>,
                    response: Response<List<Question>>
                ) {
                    if (response.isSuccessful) {
                        questions = response.body() ?: emptyList()
                        if (questions.isEmpty()) {
                            Toast.makeText(this@LiveQuizActivity,
                                "No questions found.", Toast.LENGTH_SHORT).show()
                            finish()
                            return
                        }
                        showQuestion()
                    } else {
                        Toast.makeText(this@LiveQuizActivity,
                            "Failed to load questions.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                    Toast.makeText(this@LiveQuizActivity,
                        "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
    }

    private fun showQuestion() {
        val question = questions[currentIndex]
        selectedAnswer = null
        nextButton.isEnabled = false

        // Update progress
        val progress = ((currentIndex + 1).toFloat() / questions.size * 100).toInt()
        questionProgress.progress = progress
        questionCountText.text = "Question ${currentIndex + 1}/${questions.size}"
        questionText.text = question.question

        // Set option buttons
        val optionButtons = listOf(option1Button, option2Button, option3Button, option4Button)
        optionButtons.forEachIndexed { index, btn ->
            if (index < question.options.size) {
                btn.text = question.options[index]
                btn.visibility = android.view.View.VISIBLE
                // Reset style
                btn.setBackgroundColor(android.graphics.Color.WHITE)
                btn.setTextColor(android.graphics.Color.parseColor("#2C2C2A"))
            } else {
                btn.visibility = android.view.View.GONE
            }
        }

        // Update next button label
        nextButton.text = if (currentIndex == questions.size - 1) "Finish ✓" else "Next →"
    }

    private fun onOptionSelected(selected: MaterialButton) {
        selectedAnswer = selected.text.toString()
        nextButton.isEnabled = true

        // Reset all buttons
        listOf(option1Button, option2Button, option3Button, option4Button).forEach { btn ->
            btn.setBackgroundColor(android.graphics.Color.WHITE)
            btn.setTextColor(android.graphics.Color.parseColor("#2C2C2A"))
        }

        // Highlight selected
        selected.setBackgroundColor(android.graphics.Color.parseColor("#FF6B00"))
        selected.setTextColor(android.graphics.Color.WHITE)
    }

    private fun submitScore() {
        RetrofitClient.apiService.submitScore(
            "Bearer $token",
            roomCode,
            mapOf("score" to totalScore)
        ).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                // Go to leaderboard regardless
                val intent = Intent(this@LiveQuizActivity, LeaderboardActivity::class.java)
                intent.putExtra("room_code", roomCode)
                intent.putExtra("jwt_token", token)
                intent.putExtra("my_score", totalScore)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                // Still go to leaderboard even if submit fails
                val intent = Intent(this@LiveQuizActivity, LeaderboardActivity::class.java)
                intent.putExtra("room_code", roomCode)
                intent.putExtra("jwt_token", token)
                intent.putExtra("my_score", totalScore)
                startActivity(intent)
                finish()
            }
        })
    }
}