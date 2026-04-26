package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
import com.google.android.material.appbar.MaterialToolbar
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
    private lateinit var submitButton: MaterialButton
    private lateinit var topAppBar: MaterialToolbar

    private var quizId: Long = -1
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_question)

        // Bind views
        topAppBar            = findViewById(R.id.topAppBar)
        questionInput        = findViewById(R.id.questionInput)
        option1Input         = findViewById(R.id.option1Input)
        option2Input         = findViewById(R.id.option2Input)
        option3Input         = findViewById(R.id.option3Input)
        option4Input         = findViewById(R.id.option4Input)
        correctAnswerSpinner = findViewById(R.id.correctAnswerSpinner)
        scoreInput           = findViewById(R.id.scoreText)
        submitButton         = findViewById(R.id.submitQuizButton)

        // ✅ Get quizId and token
        quizId = intent.getLongExtra("quiz_id", -1)
        token  = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)

        // ✅ Debug — check token and quizId are received
        Log.d("AddQuestion", "quizId: $quizId")
        Log.d("AddQuestion", "token: $token")

        if (token == null || quizId == -1L) {
            Toast.makeText(this, "Invalid session or quiz.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        topAppBar.setNavigationOnClickListener { finish() }

        fun refreshSpinner() {
            val options = listOf(
                option1Input.text.toString(),
                option2Input.text.toString(),
                option3Input.text.toString(),
                option4Input.text.toString()
            ).filter { it.isNotEmpty() }

            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
            correctAnswerSpinner.setAdapter(adapter)
            correctAnswerSpinner.setText("", false)
        }

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { refreshSpinner() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        option1Input.addTextChangedListener(watcher)
        option2Input.addTextChangedListener(watcher)
        option3Input.addTextChangedListener(watcher)
        option4Input.addTextChangedListener(watcher)

        submitButton.setOnClickListener {
            val questionStr    = questionInput.text.toString().trim()
            val o1             = option1Input.text.toString().trim()
            val o2             = option2Input.text.toString().trim()
            val o3             = option3Input.text.toString().trim()
            val o4             = option4Input.text.toString().trim()
            val selectedAnswer = correctAnswerSpinner.text.toString().trim()
            val scoreValue     = scoreInput.text.toString().toIntOrNull()

            if (questionStr.isEmpty()) {
                Toast.makeText(this, "Question text is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty()) {
                Toast.makeText(this, "All 4 options are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(this, "Please select the correct answer.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (scoreValue == null || scoreValue < 0) {
                Toast.makeText(this, "Enter a valid score.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val question = Question(
                id            = null,
                quizId        = quizId,
                question      = questionStr,
                options       = listOf(o1, o2, o3, o4),
                correctAnswer = selectedAnswer,
                score         = scoreValue
            )

            // ✅ Debug — confirm what is being sent
            Log.d("AddQuestion", "Sending to: api/questions/quiz/$quizId")
            Log.d("AddQuestion", "Auth header: Bearer $token")
            Log.d("AddQuestion", "Question: $question")

            submitButton.isEnabled = false

            RetrofitClient.apiService.createQuestion("Bearer $token", quizId, question)
                .enqueue(object : Callback<Question> {
                    override fun onResponse(call: Call<Question>, response: Response<Question>) {
                        submitButton.isEnabled = true
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
                        submitButton.isEnabled = true
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
}