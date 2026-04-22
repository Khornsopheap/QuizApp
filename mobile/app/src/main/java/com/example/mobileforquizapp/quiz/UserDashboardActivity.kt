package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
import com.example.mobileforquizapp.quiz.model.Quiz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        val prefs = getSharedPreferences("MyApp", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        val recyclerView = findViewById<RecyclerView>(R.id.userQuizRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch quizzes
        RetrofitClient.apiService.getQuizzes("Bearer $token")
            .enqueue(object : Callback<List<Quiz>> {
                override fun onResponse(call: Call<List<Quiz>>, response: Response<List<Quiz>>) {
                    if (response.isSuccessful) {
                        val quizzes = response.body() ?: emptyList()
                        recyclerView.adapter = QuizListAdapter(quizzes) { quiz ->
                            val intent = Intent(this@UserDashboardActivity, QuizActivity::class.java)
                            intent.putExtra("quiz_id", quiz.id)
                            intent.putExtra("jwt_token", token)
                            startActivity(intent)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {
                    // handle error
                }
            })


    }
}