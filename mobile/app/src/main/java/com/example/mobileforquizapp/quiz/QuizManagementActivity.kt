package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizManagementActivity : BaseActivity() {

    override fun currentNavItem() = NAV_QUIZZES

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuizManagementAdapter
    private lateinit var fabAddQuiz: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_management)

        setupNav()

        recyclerView = findViewById(R.id.quizManagementRecyclerView)
        fabAddQuiz   = findViewById(R.id.fabAddQuiz)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = QuizManagementAdapter(
            quizzes       = emptyList(),
            onStartClick  = { quiz ->
                startActivity(
                    Intent(this, AdminQuizDetailActivity::class.java).apply {
                        putExtra("quiz_id",   quiz.id)
                        putExtra("jwt_token", token)
                    }
                )
            },
            onEditClick = { quiz ->
                // ✅ Fixed: go to CreateQuizActivity in EDIT mode
                startActivity(
                    Intent(this, CreateQuizActivity::class.java).apply {
                        putExtra("quiz_id",         quiz.id)
                        putExtra("jwt_token",        token)
                        putExtra("quiz_title",       quiz.title)
                        putExtra("quiz_description", quiz.description)
                        putExtra("edit_mode",        true)   // flag so CreateQuizActivity knows it's editing
                    }
                )
            },
            onDeleteClick = { quiz ->
                showDeleteConfirmDialog(quiz)
            }
        )
        recyclerView.adapter = adapter

        fabAddQuiz.setOnClickListener {
            startActivity(
                Intent(this, CreateQuizActivity::class.java).apply {
                    putExtra("jwt_token", token)
                    // no quiz_id → CreateQuizActivity treats this as a new quiz
                }
            )
        }

        loadQuizzes()
    }

    override fun onResume() {
        super.onResume()
        loadQuizzes()
    }

    private fun showDeleteConfirmDialog(quiz: Quiz) {
        AlertDialog.Builder(this)
            .setTitle("Delete Quiz")
            .setMessage("Delete \"${quiz.title}\"?\nThis cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteQuiz(quiz) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteQuiz(quiz: Quiz) {
        val id = quiz.id ?: run {
            Toast.makeText(this, "Invalid quiz ID.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.apiService.deleteQuiz("Bearer $token", id)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@QuizManagementActivity,
                            "\"${quiz.title}\" deleted.",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadQuizzes()
                    } else {
                        Toast.makeText(
                            this@QuizManagementActivity,
                            "Delete failed (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        this@QuizManagementActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun loadQuizzes() {
        RetrofitClient.apiService.getQuizzes("Bearer $token")
            .enqueue(object : Callback<List<Quiz>> {
                override fun onResponse(
                    call: Call<List<Quiz>>,
                    response: Response<List<Quiz>>
                ) {
                    if (response.isSuccessful) {
                        val quizzes = response.body() ?: emptyList()
                        adapter.updateList(quizzes)

                        findViewById<TextView>(R.id.tvStatTotal).text     = quizzes.size.toString()
                        findViewById<TextView>(R.id.tvStatActive).text    = quizzes.size.toString()
                        findViewById<TextView>(R.id.tvStatQuestions).text =
                            quizzes.sumOf { it.questionCount ?: 0 }.toString()
                    } else {
                        Toast.makeText(
                            this@QuizManagementActivity,
                            "Failed to load quizzes (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {
                    Toast.makeText(
                        this@QuizManagementActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}