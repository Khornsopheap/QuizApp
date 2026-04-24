package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditQuestionActivity : AppCompatActivity() {

    private lateinit var questionText: EditText
    private lateinit var optionsText: EditText
    private lateinit var correctAnswerText: EditText
    private lateinit var scoreText: EditText
    private lateinit var saveBtn: Button

    private var questionId: Long = -1
    private var quizId: Long = -1
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_question)

        questionText = findViewById(R.id.editQuestionText)
        optionsText = findViewById(R.id.editOptionsText)
        correctAnswerText = findViewById(R.id.editCorrectAnswerText)
        scoreText = findViewById(R.id.editScoreText)
        saveBtn = findViewById(R.id.saveButton)

        questionId = intent.getLongExtra("question_id", -1)
        quizId = intent.getLongExtra("quiz_id", -1)
        token = getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)

        questionText.setText(intent.getStringExtra("question_text"))
        optionsText.setText(intent.getStringExtra("question_options"))
        correctAnswerText.setText(intent.getStringExtra("question_answer"))
        scoreText.setText(intent.getIntExtra("question_score", 1).toString())

        saveBtn.setOnClickListener {
            val scoreValue = scoreText.text.toString().toIntOrNull() ?: 0

            val updated = Question(
                id = questionId,
                quizId = quizId,
                question = questionText.text.toString(),
                options = optionsText.text.toString().split(",").map { it.trim() },
                correctAnswer = correctAnswerText.text.toString(),
                score = scoreValue
            )

            RetrofitClient.apiService.updateQuestion("Bearer $token", questionId, updated)
                .enqueue(object : Callback<Question> {
                    override fun onResponse(call: Call<Question>, response: Response<Question>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@EditQuestionActivity, "Question updated!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@EditQuestionActivity, "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Question>, t: Throwable) {
                        Toast.makeText(this@EditQuestionActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
