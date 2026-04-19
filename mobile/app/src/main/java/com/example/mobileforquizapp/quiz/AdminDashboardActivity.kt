package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import com.example.mobileforquizapp.R
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.network.RetrofitClient
import android.widget.*
import com.example.mobileforquizapp.quiz.model.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val prefs = getSharedPreferences("MyApp", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        // Fetch quizzes
        RetrofitClient.apiService.getQuizzes("Bearer $token")
            .enqueue(object : Callback<List<Question>> {
                override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                    if (response.isSuccessful) {
                        val quizzes = response.body() ?: emptyList()
                        // TODO: bind quizzes to RecyclerView adapter
                    }
                }
                override fun onFailure(call: Call<List<Question>>, t: Throwable) {}
            })

        // Create quiz button
        findViewById<Button>(R.id.createQuizButton).setOnClickListener {
            startActivity(Intent(this, CreateQuizActivity::class.java).apply {
                putExtra("jwt_token", token)
            })
        }
    }
}
