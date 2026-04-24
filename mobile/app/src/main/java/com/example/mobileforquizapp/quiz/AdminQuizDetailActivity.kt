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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminQuizDetailActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuestionListAdapter
    private var quizId: Long = -1
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_quiz_detail)

        recyclerView = findViewById(R.id.questionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        quizId = intent.getLongExtra("quiz_id", -1)
        token = intent.getStringExtra("jwt_token")

        loadQuestions()
    }

    private fun loadQuestions() {
        RetrofitClient.apiService.getQuestionsByQuizId("Bearer $token", quizId)
            .enqueue(object : Callback<List<Question>> {
                override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                    if (response.isSuccessful) {
                        val questions = response.body()?.toMutableList() ?: mutableListOf()
                        adapter = QuestionListAdapter(
                            questions,
                            onEdit = { question ->
                                val intent = Intent(this@AdminQuizDetailActivity, EditQuestionActivity::class.java)
                                intent.putExtra("question_id", question.id)
                                intent.putExtra("quiz_id", quizId)
                                intent.putExtra("question_text", question.question)
                                intent.putExtra("question_options", question.options.joinToString(","))
                                intent.putExtra("question_answer", question.correctAnswer)
                                intent.putExtra("question_score", question.score)
                                startActivity(intent)
                            },
                            onDelete = { question ->
                                deleteQuestion(question.id)
                            }
                        )
                        recyclerView.adapter = adapter
                    }
                }

                override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                    Toast.makeText(this@AdminQuizDetailActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun deleteQuestion(questionId: Long?) {
        RetrofitClient.apiService.deleteQuestion("Bearer $token", questionId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminQuizDetailActivity, "Deleted", Toast.LENGTH_SHORT).show()
                        loadQuestions() // refresh list
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AdminQuizDetailActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
