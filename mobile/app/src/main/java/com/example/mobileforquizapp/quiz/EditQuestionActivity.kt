package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditQuestionActivity : AppCompatActivity() {

    private lateinit var questionText: TextInputEditText
    private lateinit var option1: TextInputEditText
    private lateinit var option2: TextInputEditText
    private lateinit var option3: TextInputEditText
    private lateinit var option4: TextInputEditText
    private lateinit var correctAnswerText: TextInputEditText
    private lateinit var scoreText: TextInputEditText
    private lateinit var saveBtn: MaterialButton

    private var questionId: Long = -1
    private var quizId: Long = -1
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Uses the new activity_edit_question.xml (created to match new design style)
        setContentView(R.layout.activity_edit_question)

        // IDs are unchanged — activity_edit_question.xml preserves original IDs
        questionText      = findViewById(R.id.editQuestionText)
        option1           = findViewById(R.id.editOption1)
        option2           = findViewById(R.id.editOption2)
        option3           = findViewById(R.id.editOption3)
        option4           = findViewById(R.id.editOption4)
        correctAnswerText = findViewById(R.id.editCorrectAnswerText)
        scoreText         = findViewById(R.id.editScoreText)
        saveBtn           = findViewById(R.id.saveButton)

        // NEW: backButton is now an ImageView (backBtn style) in the new layout
        findViewById<ImageView>(R.id.backButton).setOnClickListener { finish() }

        questionId = intent.getLongExtra("question_id", -1)
        quizId     = intent.getLongExtra("quiz_id", -1)
        token      = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)

        if (token == null || questionId == -1L || quizId == -1L) {
            Toast.makeText(this, "Invalid data or session.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        questionText.setText(intent.getStringExtra("question_text"))
        correctAnswerText.setText(intent.getStringExtra("question_answer"))
        scoreText.setText(intent.getIntExtra("question_score", 1).toString())

        val options = intent.getStringExtra("question_options")
            ?.split(",")?.map { it.trim() } ?: emptyList()
        option1.setText(options.getOrNull(0) ?: "")
        option2.setText(options.getOrNull(1) ?: "")
        option3.setText(options.getOrNull(2) ?: "")
        option4.setText(options.getOrNull(3) ?: "")

        saveBtn.setOnClickListener {
            val questionStr = questionText.text.toString().trim()
            val o1          = option1.text.toString().trim()
            val o2          = option2.text.toString().trim()
            val o3          = option3.text.toString().trim()
            val o4          = option4.text.toString().trim()
            val answerStr   = correctAnswerText.text.toString().trim()
            val scoreValue  = scoreText.text.toString().toIntOrNull()

            if (questionStr.isEmpty()) {
                Toast.makeText(this, "Question text is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty()) {
                Toast.makeText(this, "All 4 options are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (answerStr.isEmpty()) {
                Toast.makeText(this, "Correct answer is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (scoreValue == null || scoreValue < 0) {
                Toast.makeText(this, "Enter a valid score.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updated = Question(
                id            = questionId,
                quizId        = quizId,
                question      = questionStr,
                options       = listOf(o1, o2, o3, o4),
                correctAnswer = answerStr,
                score         = scoreValue
            )

            saveBtn.isEnabled = false

            RetrofitClient.apiService.updateQuestion("Bearer $token", questionId, updated)
                .enqueue(object : Callback<Question> {
                    override fun onResponse(call: Call<Question>, response: Response<Question>) {
                        saveBtn.isEnabled = true
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@EditQuestionActivity,
                                "Question updated!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(
                                this@EditQuestionActivity,
                                "Update failed (${response.code()}): $errorBody",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Question>, t: Throwable) {
                        saveBtn.isEnabled = true
                        Toast.makeText(
                            this@EditQuestionActivity,
                            "Network error: ${t.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}
