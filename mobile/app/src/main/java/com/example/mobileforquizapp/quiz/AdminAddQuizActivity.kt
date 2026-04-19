package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminAddQuizActivity : AppCompatActivity() {

    private lateinit var questionInput: EditText
    private lateinit var option1Input: EditText
    private lateinit var option2Input: EditText
    private lateinit var option3Input: EditText
    private lateinit var option4Input: EditText
    private lateinit var correctAnswerSpinner: Spinner
    private lateinit var scoreInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_quiz)

        questionInput = findViewById(R.id.questionInput)
        option1Input = findViewById(R.id.option1Input)
        option2Input = findViewById(R.id.option2Input)
        option3Input = findViewById(R.id.option3Input)
        option4Input = findViewById(R.id.option4Input)
        correctAnswerSpinner = findViewById(R.id.correctAnswerSpinner)
        scoreInput = findViewById(R.id.scoreText)
        submitButton = findViewById(R.id.submitQuizButton)

        // Initialize spinner with empty list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf<String>())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        correctAnswerSpinner.adapter = adapter

        // Function to refresh spinner whenever options change
        fun refreshSpinner() {
            val options = listOf(
                option1Input.text.toString(),
                option2Input.text.toString(),
                option3Input.text.toString(),
                option4Input.text.toString()
            ).filter { it.isNotEmpty() } // only non-empty options

            val newAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
            newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            correctAnswerSpinner.adapter = newAdapter
        }

        // Add TextWatchers to update spinner live
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { refreshSpinner() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        option1Input.addTextChangedListener(watcher)
        option2Input.addTextChangedListener(watcher)
        option3Input.addTextChangedListener(watcher)
        option4Input.addTextChangedListener(watcher)

        // Submit button
        submitButton.setOnClickListener {
            val options = listOf(
                option1Input.text.toString(),
                option2Input.text.toString(),
                option3Input.text.toString(),
                option4Input.text.toString()
            ).filter { it.isNotEmpty() }

            val selectedAnswer = correctAnswerSpinner.selectedItem?.toString() ?: ""
            val score = scoreInput.text.toString().toIntOrNull() ?: 0

            val question = Question(
                id = null,
                question = questionInput.text.toString(),
                options = options,
                correctAnswer = selectedAnswer,
                score = score
            )

            val token = getSharedPreferences("MyApp", MODE_PRIVATE)
                .getString("jwt_token", "") ?: ""

            RetrofitClient.apiService.addQuiz("Bearer $token", question)
                .enqueue(object : Callback<Question> {
                    override fun onResponse(call: Call<Question>, response: Response<Question>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AdminAddQuizActivity, "Quiz added successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Log.e("AdminAddQuiz", "Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Question>, t: Throwable) {
                        Log.e("AdminAddQuiz", "Failed to add quiz", t)
                    }
                })
        }
    }
}
