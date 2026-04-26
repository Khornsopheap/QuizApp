package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
import com.example.mobileforquizapp.quiz.model.Quiz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateQuizActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quiz)

        val titleInput = findViewById<EditText>(R.id.quizTitleInput)
        val descriptionInput = findViewById<EditText>(R.id.quizDescriptionInput)
        val createButton = findViewById<Button>(R.id.createQuizButton)

        val prefs = getSharedPreferences("MyApp", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        createButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quiz = Quiz(title = title, description = description)

            RetrofitClient.apiService.createQuiz("Bearer $token", quiz)
                .enqueue(object : Callback<Quiz> {
                    override fun onResponse(call: Call<Quiz>, response: Response<Quiz>) {
                        if (response.isSuccessful) {
                            val createdQuiz = response.body()
                            Toast.makeText(this@CreateQuizActivity, "Quiz created with ID: ${createdQuiz?.id}", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@CreateQuizActivity, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Quiz>, t: Throwable) {
                        Toast.makeText(this@CreateQuizActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
