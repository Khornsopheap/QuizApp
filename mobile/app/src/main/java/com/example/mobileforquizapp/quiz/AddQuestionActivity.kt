package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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

class AddQuestionActivity : AppCompatActivity() {

    private lateinit var questionInput: TextInputEditText
    private lateinit var option1Input: TextInputEditText
    private lateinit var option2Input: TextInputEditText
    private lateinit var option3Input: TextInputEditText
    private lateinit var option4Input: TextInputEditText
    private lateinit var correctAnswerSpinner: AutoCompleteTextView
    private lateinit var scoreInput: TextInputEditText
    private lateinit var saveQuestionBtn: MaterialButton

    private var quizId: Long = -1
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_question)

        // Bind views
        questionInput        = findViewById(R.id.questionInput)
        option1Input         = findViewById(R.id.option1Input)
        option2Input         = findViewById(R.id.option2Input)
        option3Input         = findViewById(R.id.option3Input)
        option4Input         = findViewById(R.id.option4Input)
        correctAnswerSpinner = findViewById(R.id.correctAnswerSpinner)
        scoreInput           = findViewById(R.id.scoreInput)
        saveQuestionBtn      = findViewById(R.id.saveQuestionBtn)

        // Back button
        findViewById<MaterialButton>(R.id.backBtn).setOnClickListener { finish() }

        quizId = intent.getLongExtra("quiz_id", -1)
        token  = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)

        Log.d("AddQuestion", "quizId: $quizId")
        Log.d("AddQuestion", "token: $token")

        if (token == null || quizId == -1L) {
            Toast.makeText(this, "Invalid session or quiz.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Populate the correct answer dropdown when the user focuses it
        correctAnswerSpinner.setOnClickListener {
            refreshCorrectAnswerDropdown()
        }
        correctAnswerSpinner.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) refreshCorrectAnswerDropdown()
        }

        saveQuestionBtn.setOnClickListener {
            submitQuestion()
        }
    }

    private fun refreshCorrectAnswerDropdown() {
        val options = listOf(
            option1Input.text.toString().trim(),
            option2Input.text.toString().trim(),
            option3Input.text.toString().trim(),
            option4Input.text.toString().trim()
        ).filter { it.isNotEmpty() }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        correctAnswerSpinner.setAdapter(adapter)
        correctAnswerSpinner.showDropDown()
    }

    private fun submitQuestion() {
        val questionStr   = questionInput.text.toString().trim()
        val opt1          = option1Input.text.toString().trim()
        val opt2          = option2Input.text.toString().trim()
        val opt3          = option3Input.text.toString().trim()
        val opt4          = option4Input.text.toString().trim()
        val correctAnswer = correctAnswerSpinner.text.toString().trim()
        val scoreStr      = scoreInput.text.toString().trim()

        // Validation
        if (questionStr.isEmpty()) {
            Toast.makeText(this, "Question text is required.", Toast.LENGTH_SHORT).show()
            return
        }

        val options = listOf(opt1, opt2, opt3, opt4).filter { it.isNotEmpty() }

        if (options.size < 2) {
            Toast.makeText(this, "At least 2 options are required.", Toast.LENGTH_SHORT).show()
            return
        }
        if (correctAnswer.isEmpty()) {
            Toast.makeText(this, "Please select the correct answer.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!options.contains(correctAnswer)) {
            Toast.makeText(this, "Correct answer must match one of the options.", Toast.LENGTH_SHORT).show()
            return
        }
        if (scoreStr.isEmpty()) {
            Toast.makeText(this, "Please enter a score.", Toast.LENGTH_SHORT).show()
            return
        }

        val score = scoreStr.toIntOrNull() ?: run {
            Toast.makeText(this, "Score must be a valid number.", Toast.LENGTH_SHORT).show()
            return
        }

        val question = Question(
            id            = null,
            quizId        = quizId,
            question      = questionStr,
            options       = options,
            correctAnswer = correctAnswer,
            score         = score
        )

        Log.d("AddQuestion", "Sending to: api/questions/quiz/$quizId")
        Log.d("AddQuestion", "Auth header: Bearer $token")
        Log.d("AddQuestion", "Question: $question")

        saveQuestionBtn.isEnabled = false

        RetrofitClient.apiService.createQuestion("Bearer $token", quizId, question)
            .enqueue(object : Callback<Question> {
                override fun onResponse(call: Call<Question>, response: Response<Question>) {
                    saveQuestionBtn.isEnabled = true
                    Log.d("AddQuestion", "Response code: ${response.code()}")
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddQuestionActivity,
                            "Question added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("AddQuestion", "Error body: $errorBody")
                        Toast.makeText(
                            this@AddQuestionActivity,
                            "Failed (${response.code()}): $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Question>, t: Throwable) {
                    saveQuestionBtn.isEnabled = true
                    Log.e("AddQuestion", "Network error: ${t.message}")
                    Toast.makeText(
                        this@AddQuestionActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}