package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Question
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminQuizDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuestionListAdapter
    private lateinit var addQuestionFab: MaterialButton
    private lateinit var topAppBar: MaterialButton
    private lateinit var startQuizButton: MaterialButton  // ✅ new

    private var quizId: Long = -1
    private var token: String? = null
    private val questions = mutableListOf<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_quiz_detail)

        recyclerView     = findViewById(R.id.questionRecyclerView)
        addQuestionFab   = findViewById(R.id.addQuestionFab)
        topAppBar        = findViewById(R.id.topAppBar)
        startQuizButton  = findViewById(R.id.startQuizButton)  // ✅ new

        recyclerView.layoutManager = LinearLayoutManager(this)

        quizId = intent.getLongExtra("quiz_id", -1)
        token  = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)

        if (token == null || quizId == -1L) {
            Toast.makeText(this, "Invalid session or quiz.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        topAppBar.setOnClickListener { finish() }

        addQuestionFab.setOnClickListener {
            val intent = Intent(this, AddQuestionActivity::class.java)
            intent.putExtra("quiz_id", quizId)
            intent.putExtra("jwt_token", token)
            startActivity(intent)
        }

        // ✅ Start Quiz → call API to create room → go to RoomCodeActivity
        startQuizButton.setOnClickListener {
            if (questions.isEmpty()) {
                Toast.makeText(this, "Add at least one question first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startQuizButton.isEnabled = false
            startQuizButton.text = "Starting..."

            RetrofitClient.apiService.createSession("Bearer $token", quizId)
                .enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(
                        call: Call<Map<String, String>>,
                        response: Response<Map<String, String>>
                    ) {
                        startQuizButton.isEnabled = true
                        startQuizButton.text = "▶ Start Quiz"
                        if (response.isSuccessful) {
                            val roomCode = response.body()?.get("roomCode") ?: ""
                            val intent = Intent(this@AdminQuizDetailActivity, RoomCodeActivity::class.java)
                            intent.putExtra("room_code", roomCode)
                            intent.putExtra("quiz_id", quizId)
                            intent.putExtra("jwt_token", token)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@AdminQuizDetailActivity,
                                "Failed to start quiz (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        startQuizButton.isEnabled = true
                        startQuizButton.text = "▶ Start Quiz"
                        Toast.makeText(
                            this@AdminQuizDetailActivity,
                            "Network error: ${t.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        adapter = QuestionListAdapter(
            questions,
            onEdit = { question ->
                val intent = Intent(this, EditQuestionActivity::class.java)
                intent.putExtra("question_id", question.id)
                intent.putExtra("quiz_id", quizId)
                intent.putExtra("question_text", question.question)
                intent.putExtra("question_options", question.options.joinToString(","))
                intent.putExtra("question_answer", question.correctAnswer)
                intent.putExtra("question_score", question.score)
                intent.putExtra("jwt_token", token)
                startActivity(intent)
            },
            onDelete = { question ->
                question.id?.let { deleteQuestion(it) }
            }
        )
        recyclerView.adapter = adapter
        loadQuestions()
    }

    override fun onResume() {
        super.onResume()
        loadQuestions()
    }

    private fun loadQuestions() {
        RetrofitClient.apiService.getQuestionsByQuizId("Bearer $token", quizId)
            .enqueue(object : Callback<List<Question>> {
                override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                    if (response.isSuccessful) {
                        questions.clear()
                        questions.addAll(response.body() ?: emptyList())
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@AdminQuizDetailActivity,
                            "Load failed (${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                    Toast.makeText(this@AdminQuizDetailActivity,
                        "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun deleteQuestion(questionId: Long) {
        RetrofitClient.apiService.deleteQuestion("Bearer $token", questionId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminQuizDetailActivity,
                            "Deleted successfully", Toast.LENGTH_SHORT).show()
                        loadQuestions()
                    } else {
                        Toast.makeText(this@AdminQuizDetailActivity,
                            "Delete failed: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AdminQuizDetailActivity,
                        "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}