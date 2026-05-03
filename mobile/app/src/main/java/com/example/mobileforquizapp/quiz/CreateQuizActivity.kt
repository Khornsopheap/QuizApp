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

class CreateQuizActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quiz)

        val titleInput       = findViewById<EditText>(R.id.quizTitleInput)
        val descriptionInput = findViewById<EditText>(R.id.quizDescriptionInput)
        val createButton     = findViewById<Button>(R.id.createQuizButton)

        // ✅ Read token from intent first, fallback to SharedPreferences
        val token = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)

        Log.d("CreateQuiz", "Token present: ${!token.isNullOrEmpty()}")

        val quizId     = intent.getLongExtra("quiz_id", -1L)
        val isEditMode = quizId != -1L

        if (isEditMode) {
            titleInput.setText(intent.getStringExtra("quiz_title") ?: "")
            descriptionInput.setText(intent.getStringExtra("quiz_description") ?: "")
            createButton.text = "Save Changes"
            Log.d("CreateQuiz", "Edit mode — quizId: $quizId")
        }

        createButton.setOnClickListener {
            val title       = titleInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (token.isNullOrEmpty()) {
                Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            createButton.isEnabled = false
            val quiz       = Quiz(title = title, description = description)
            val authHeader = "Bearer $token"

            Log.d("CreateQuiz", "Calling ${if (isEditMode) "PUT quizzes/$quizId" else "POST quizzes"}")
            Log.d("CreateQuiz", "Auth header: $authHeader")

            if (isEditMode) {
                RetrofitClient.apiService.updateQuiz(authHeader, quizId, quiz)
                    .enqueue(object : Callback<Quiz> {
                        override fun onResponse(call: Call<Quiz>, response: Response<Quiz>) {
                            createButton.isEnabled = true
                            Log.d("CreateQuiz", "Update response: ${response.code()}")
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@CreateQuizActivity,
                                    "Quiz updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                val err = response.errorBody()?.string()
                                Log.e("CreateQuiz", "Update error body: $err")
                                Toast.makeText(
                                    this@CreateQuizActivity,
                                    "Update failed: ${response.code()}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        override fun onFailure(call: Call<Quiz>, t: Throwable) {
                            createButton.isEnabled = true
                            Log.e("CreateQuiz", "Network error: ${t.message}")
                            Toast.makeText(this@CreateQuizActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                RetrofitClient.apiService.createQuiz(authHeader, quiz)
                    .enqueue(object : Callback<Quiz> {
                        override fun onResponse(call: Call<Quiz>, response: Response<Quiz>) {
                            createButton.isEnabled = true
                            Log.d("CreateQuiz", "Create response: ${response.code()}")
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@CreateQuizActivity,
                                    "Quiz created with ID: ${response.body()?.id}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Toast.makeText(
                                    this@CreateQuizActivity,
                                    "Failed: ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        override fun onFailure(call: Call<Quiz>, t: Throwable) {
                            createButton.isEnabled = true
                            Toast.makeText(this@CreateQuizActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }
}