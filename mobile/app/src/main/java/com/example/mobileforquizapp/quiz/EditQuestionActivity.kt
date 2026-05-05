package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
import com.google.android.material.button.MaterialButton
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
    private lateinit var correctAnswerText: AutoCompleteTextView
    private lateinit var scoreText: TextInputEditText
    private lateinit var saveBtn: MaterialButton

    private var questionId: Long = -1
    private var quizId: Long = -1
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_question)

        questionText      = findViewById(R.id.questionInput)
        option1           = findViewById(R.id.option1Input)
        option2           = findViewById(R.id.option2Input)
        option3           = findViewById(R.id.option3Input)
        option4           = findViewById(R.id.option4Input)
        correctAnswerText = findViewById(R.id.correctAnswerSpinner)
        scoreText         = findViewById(R.id.scoreInput)
        saveBtn           = findViewById(R.id.saveQuestionBtn)

        val title   = findViewById<TextView>(R.id.titleText)
        val backBtn = findViewById<MaterialButton>(R.id.backBtn)

        title.text   = "Edit Question"
        saveBtn.text = "Update Question"
        backBtn.setOnClickListener { finish() }

        questionId = intent.getLongExtra("question_id", -1)
        quizId     = intent.getLongExtra("quiz_id", -1)
        token      = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)

        if (token == null || questionId == -1L || quizId == -1L) {
            Toast.makeText(this, "Invalid data or session.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Prefill question text and score
        questionText.setText(intent.getStringExtra("question_text"))
        scoreText.setText(intent.getIntExtra("question_score", 1).toString())

        // Prefill options
        val options = intent.getStringExtra("question_options")
            ?.split(",")?.map { it.trim() } ?: emptyList()

        option1.setText(options.getOrNull(0) ?: "")
        option2.setText(options.getOrNull(1) ?: "")
        option3.setText(options.getOrNull(2) ?: "")
        option4.setText(options.getOrNull(3) ?: "")

        // Setup dropdown with the 4 options
        setupCorrectAnswerDropdown(options)

        // Prefill selected correct answer
        val savedAnswer = intent.getStringExtra("question_answer") ?: ""
        correctAnswerText.setText(savedAnswer, false)

        // When any option text changes, refresh the dropdown
        val watcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setupCorrectAnswerDropdown(getCurrentOptions())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        }
        option1.addTextChangedListener(watcher)
        option2.addTextChangedListener(watcher)
        option3.addTextChangedListener(watcher)
        option4.addTextChangedListener(watcher)

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
                Toast.makeText(this, "Please select the correct answer.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!listOf(o1, o2, o3, o4).contains(answerStr)) {
                Toast.makeText(this, "Correct answer must match one of the 4 options.", Toast.LENGTH_SHORT).show()
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

            RetrofitClient.apiService
                .updateQuestion("Bearer $token", questionId, updated)
                .enqueue(object : Callback<Question> {
                    override fun onResponse(
                        call: Call<Question>,
                        response: Response<Question>
                    ) {
                        saveBtn.isEnabled = true
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@EditQuestionActivity,
                                "Question updated!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@EditQuestionActivity,
                                "Update failed (${response.code()})",
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

    private fun getCurrentOptions(): List<String> {
        return listOf(
            option1.text.toString().trim(),
            option2.text.toString().trim(),
            option3.text.toString().trim(),
            option4.text.toString().trim()
        ).filter { it.isNotEmpty() }
    }

    private fun setupCorrectAnswerDropdown(options: List<String>) {
        val filtered = options.filter { it.isNotEmpty() }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            filtered
        )
        correctAnswerText.setAdapter(adapter)
        correctAnswerText.threshold = 0

        // Show dropdown on click even if already has text
        correctAnswerText.setOnClickListener {
            correctAnswerText.showDropDown()
        }
        correctAnswerText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) correctAnswerText.showDropDown()
        }
    }
}