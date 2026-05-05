package com.example.mobileforquizapp.quiz
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val scoreText = findViewById<TextView>(R.id.scoreText)

        val score = intent.getIntExtra("score", 0)
        val feedback = intent.getStringExtra("feedback") ?: ""

        scoreText.text = "Your Score: $score"
//        feedbackText.text = feedback
    }
}
