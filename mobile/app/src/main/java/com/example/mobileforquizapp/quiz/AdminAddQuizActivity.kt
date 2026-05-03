package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminAddQuizActivity : AppCompatActivity() {

    private lateinit var questionInput: EditText
    private lateinit var optionsContainer: LinearLayout
    private lateinit var saveQuestionBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_question)

        questionInput    = findViewById(R.id.questionInput)
        optionsContainer = findViewById(R.id.optionsContainer)
        saveQuestionBtn  = findViewById(R.id.saveQuestionBtn)

        val quizId = intent.getLongExtra("quiz_id", -1)
        val token  = getSharedPreferences("MyApp", MODE_PRIVATE)
            .getString("jwt_token", "") ?: ""

        // Inflate 4 option rows dynamically
        val inflater = LayoutInflater.from(this)
        repeat(4) { index ->
            val optionView = inflater.inflate(R.layout.item_option_editor, optionsContainer, false)
            optionView.tag = "option_$index"

            optionView.findViewById<ImageView>(R.id.deleteOptionBtn).setOnClickListener {
                if (optionsContainer.childCount > 2) {
                    optionsContainer.removeView(optionView)
                } else {
                    Toast.makeText(this, "Minimum 2 options required.", Toast.LENGTH_SHORT).show()
                }
            }
            optionsContainer.addView(optionView)
        }

        saveQuestionBtn.setOnClickListener {
            val questionStr = questionInput.text.toString().trim()

            if (questionStr.isEmpty()) {
                Toast.makeText(this, "Question text is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val options = mutableListOf<String>()
            var correctAnswer = ""

            for (i in 0 until optionsContainer.childCount) {
                val optionView = optionsContainer.getChildAt(i)
                val text      = optionView.findViewById<EditText>(R.id.optionInput).text.toString().trim()
                val isCorrect = optionView.findViewById<CheckBox>(R.id.correctToggle).isChecked

                if (text.isNotEmpty()) {
                    options.add(text)
                    if (isCorrect) correctAnswer = text
                }
            }

            if (options.size < 2) {
                Toast.makeText(this, "At least 2 options are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (correctAnswer.isEmpty()) {
                Toast.makeText(this, "Please mark the correct answer.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val question = Question(
                id            = null,
                quizId        = quizId,
                question      = questionStr,
                options       = options,
                correctAnswer = correctAnswer,
                score         = 10
            )

            saveQuestionBtn.isEnabled = false

            RetrofitClient.apiService.addQuiz("Bearer $token", question)
                .enqueue(object : Callback<Question> {
                    override fun onResponse(call: Call<Question>, response: Response<Question>) {
                        saveQuestionBtn.isEnabled = true
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@AdminAddQuizActivity,
                                "Quiz added successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Log.e("AdminAddQuiz", "Error: ${response.code()}")
                            Toast.makeText(
                                this@AdminAddQuizActivity,
                                "Failed (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Question>, t: Throwable) {
                        saveQuestionBtn.isEnabled = true
                        Log.e("AdminAddQuiz", "Failed to add quiz", t)
                        Toast.makeText(
                            this@AdminAddQuizActivity,
                            "Network error: ${t.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}