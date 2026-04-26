package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.login.LoginActivity
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var joinGameButton: MaterialButton
    private lateinit var createQuizButton: MaterialButton

    private var token: String? = null
    private var isAdmin: Boolean = false
    private val quizzes = mutableListOf<Quiz>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        recyclerView      = findViewById(R.id.quizRecyclerView)
        topAppBar         = findViewById(R.id.topAppBar)
        joinGameButton    = findViewById(R.id.joinGameButton)
        createQuizButton  = findViewById(R.id.createQuizButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Get token and role from intent or SharedPreferences
        token   = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)
        isAdmin = intent.getBooleanExtra("is_admin", false).let {
            if (!it) getSharedPreferences("MyApp", MODE_PRIVATE)
                .getBoolean("is_admin", false)
            else it
        }

        if (token == null) {
            goToLogin()
            return
        }

        // ✅ Show correct buttons based on role
        if (isAdmin) {
            createQuizButton.visibility = android.view.View.VISIBLE
            joinGameButton.visibility   = android.view.View.GONE
        } else {
            joinGameButton.visibility   = android.view.View.VISIBLE
            createQuizButton.visibility = android.view.View.GONE
        }

        // ✅ Logout
        topAppBar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.action_logout) {
                getSharedPreferences("MyApp", MODE_PRIVATE).edit().clear().apply()
                goToLogin()
                true
            } else false
        }

        // ✅ Join Game button — users only
        joinGameButton.setOnClickListener {
            val intent = Intent(this, JoinRoomActivity::class.java)
            intent.putExtra("jwt_token", token)
            startActivity(intent)
        }

        // ✅ Create Quiz button — admin only
        createQuizButton.setOnClickListener {
            val intent = Intent(this, CreateQuizActivity::class.java)
            intent.putExtra("jwt_token", token)
            startActivity(intent)
        }

        // ✅ Quiz adapter — tapping a quiz goes to detail
        val adapter = QuizListAdapter(
            quizzes,
            isAdmin = isAdmin,
            onQuizClick = { quiz ->
                if (isAdmin) {
                    val intent = Intent(this, AdminQuizDetailActivity::class.java)
                    intent.putExtra("quiz_id", quiz.id)
                    intent.putExtra("jwt_token", token)
                    startActivity(intent)
                } else {
                    // Users can just view — joining happens via room code
                    Toast.makeText(this,
                        "Use Join Game to play a live quiz!", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter

        loadQuizzes(adapter)
    }

    override fun onResume() {
        super.onResume()
        // Refresh list when coming back
        val adapter = recyclerView.adapter as? QuizListAdapter
        adapter?.let { loadQuizzes(it) }
    }

    private fun loadQuizzes(adapter: QuizListAdapter) {
        RetrofitClient.apiService.getQuizzes("Bearer $token")
            .enqueue(object : Callback<List<Quiz>> {
                override fun onResponse(
                    call: Call<List<Quiz>>,
                    response: Response<List<Quiz>>
                ) {
                    if (response.isSuccessful) {
                        quizzes.clear()
                        quizzes.addAll(response.body() ?: emptyList())
                        adapter.notifyDataSetChanged()
                    } else if (response.code() == 403) {
                        Toast.makeText(this@QuizListActivity,
                            "Session expired. Please log in again.",
                            Toast.LENGTH_SHORT).show()
                        goToLogin()
                    } else {
                        Toast.makeText(this@QuizListActivity,
                            "Failed to load quizzes (${response.code()})",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {
                    Toast.makeText(this@QuizListActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}