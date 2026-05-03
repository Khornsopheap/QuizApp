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

class AddQuestionActivity : AppCompatActivity() {

    private lateinit var questionInput: EditText
    private lateinit var optionsContainer: LinearLayout
    private lateinit var saveQuestionBtn: MaterialButton  // NEW: was submitQuizButton

    private var quizId: Long = -1
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_question)

        // Bind views
        questionInput    = findViewById(R.id.questionInput)
        optionsContainer = findViewById(R.id.optionsContainer)
        saveQuestionBtn  = findViewById(R.id.saveQuestionBtn)  // NEW: was submitQuizButton

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

        // Add 4 option rows dynamically into optionsContainer
        val inflater = LayoutInflater.from(this)
        repeat(4) { index ->
            val optionView = inflater.inflate(R.layout.item_option_editor, optionsContainer, false)
            optionView.tag = "option_$index"

            // Wire delete button
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

            // Collect options and detect correct answer
            val options = mutableListOf<String>()
            var correctAnswer = ""

            for (i in 0 until optionsContainer.childCount) {
                val optionView = optionsContainer.getChildAt(i)
                val text   = optionView.findViewById<EditText>(R.id.optionInput).text.toString().trim()
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
                score         = 10   // default score; update if you add a score field
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
}
