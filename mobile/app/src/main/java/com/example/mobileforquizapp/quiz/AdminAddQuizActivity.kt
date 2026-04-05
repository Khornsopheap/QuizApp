package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminAddQuizActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_quiz)

        val questionInput = findViewById<EditText>(R.id.questionInput)
        val option1Input = findViewById<EditText>(R.id.option1Input)
        val option2Input = findViewById<EditText>(R.id.option2Input)
        val option3Input = findViewById<EditText>(R.id.option3Input)
        val option4Input = findViewById<EditText>(R.id.option4Input)
        val correctAnswerInput = findViewById<EditText>(R.id.correctAnswerInput)
        val submitButton = findViewById<Button>(R.id.submitQuizButton)

        submitButton.setOnClickListener {
            val quiz = Quiz(
                id = null,
                question = questionInput.text.toString(),
                options = listOf(
                    option1Input.text.toString(),
                    option2Input.text.toString(),
                    option3Input.text.toString(),
                    option4Input.text.toString()
                ),
                correctAnswer = correctAnswerInput.text.toString()
            )

            val token = getSharedPreferences("MyApp", MODE_PRIVATE)
                .getString("jwt_token", "") ?: ""

            RetrofitClient.apiService.addQuiz("Bearer $token", quiz)
                .enqueue(object : Callback<Quiz> {
                    override fun onResponse(call: Call<Quiz>, response: Response<Quiz>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@AdminAddQuizActivity,
                                "Quiz added successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.e("AdminAddQuiz", "Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Quiz>, t: Throwable) {
                        Log.e("AdminAddQuiz", "Failed to add quiz", t)
                    }
                })
        }
    }
}
