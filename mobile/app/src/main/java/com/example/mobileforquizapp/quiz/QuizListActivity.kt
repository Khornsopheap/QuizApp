package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.login.LoginActivity
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var joinGameButton: MaterialButton
    private lateinit var createQuizButton: MaterialButton
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var logoutBtn: MaterialButton

    private var token: String? = null
    private var isAdmin: Boolean = false
    private val quizzes = mutableListOf<Quiz>()
    private lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        // Bind views
        recyclerView     = findViewById(R.id.quizRecyclerView)
        joinGameButton   = findViewById(R.id.joinGameButton)
        createQuizButton = findViewById(R.id.createQuizButton)
        bottomNav        = findViewById(R.id.bottomNavigationView)
        logoutBtn        = findViewById(R.id.logoutBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get token and role
        token = intent.getStringExtra("jwt_token")
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

        // FAB visibility based on role
        if (isAdmin) {
            createQuizButton.visibility = android.view.View.VISIBLE
            joinGameButton.visibility   = android.view.View.GONE
        } else {
            joinGameButton.visibility   = android.view.View.VISIBLE
            createQuizButton.visibility = android.view.View.GONE
        }

        // Logout
        logoutBtn.setOnClickListener { logout() }

        // Bottom nav — set correct menu based on role
        bottomNav.menu.clear()
        if (isAdmin) {
            bottomNav.inflateMenu(R.menu.menu_admin_bottom_nav)
        } else {
            bottomNav.inflateMenu(R.menu.menu_user_bottom_nav)
        }
        bottomNav.selectedItemId = R.id.nav_quizzes

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(
                        Intent(this, AdminDashboardActivity::class.java).apply {
                            putExtra("jwt_token", token)
                            putExtra("is_admin", true)
                        }
                    )
                    true
                }
                R.id.nav_home -> {
                    Toast.makeText(this, "Home coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_quizzes -> true
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // FAB listeners
        joinGameButton.setOnClickListener {
            startActivity(
                Intent(this, JoinRoomActivity::class.java).apply {
                    putExtra("jwt_token", token)
                }
            )
        }

        createQuizButton.setOnClickListener {
            startActivity(
                Intent(this, CreateQuizActivity::class.java).apply {
                    putExtra("jwt_token", token)
                }
            )
        }

        // Adapter
        adapter = QuizListAdapter(
            quizzes,
            isAdmin = isAdmin,
            onQuizClick = { quiz ->
                if (isAdmin) {
                    startActivity(
                        Intent(this, AdminQuizDetailActivity::class.java).apply {
                            putExtra("quiz_id", quiz.id)
                            putExtra("jwt_token", token)
                        }
                    )
                } else {
                    Toast.makeText(
                        this,
                        "Use Join Game to play a live quiz!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        recyclerView.adapter = adapter

        loadQuizzes()
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.nav_quizzes
        loadQuizzes()
    }

    private fun loadQuizzes() {
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
                        Toast.makeText(
                            this@QuizListActivity,
                            "Session expired. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        goToLogin()
                    } else {
                        Toast.makeText(
                            this@QuizListActivity,
                            "Failed to load quizzes (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {
                    Toast.makeText(
                        this@QuizListActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun logout() {
        getSharedPreferences("MyApp", MODE_PRIVATE).edit().clear().apply()
        goToLogin()
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }
}