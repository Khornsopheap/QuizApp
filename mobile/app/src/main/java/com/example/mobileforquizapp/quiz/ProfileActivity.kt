package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.login.LoginActivity
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.switchmaterial.SwitchMaterial
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : BaseActivity() {

    override fun currentNavItem() = NAV_PROFILE

    private lateinit var profileName: TextView
    private lateinit var statQuizzes: TextView
    private lateinit var statStudents: TextView
    private lateinit var statAccuracy: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupNav()

        profileName   = findViewById(R.id.profileName)
        statQuizzes   = findViewById(R.id.statQuizzes)
        statStudents  = findViewById(R.id.statStudents)
        statAccuracy  = findViewById(R.id.statAccuracy)

        val prefs    = getSharedPreferences("MyApp", MODE_PRIVATE)
        val username = prefs.getString("username", "EduQuiz Admin") ?: "EduQuiz Admin"
        val role     = prefs.getString("role", "ADMIN") ?: "ADMIN"

        profileName.text = username

        loadStats()

        findViewById<LinearLayout>(R.id.menuPersonalInfo).setOnClickListener {
            Toast.makeText(this, "Personal Information coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.menuActivityHistory).setOnClickListener {
            Toast.makeText(this, "Activity History coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.menuNotifications).setOnClickListener {
            Toast.makeText(this, "Notification Preferences coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.menuSupport).setOnClickListener {
            Toast.makeText(this, "Support & FAQ coming soon", Toast.LENGTH_SHORT).show()
        }

        // Dark mode toggle
        val darkSwitch = findViewById<SwitchMaterial>(R.id.darkModeSwitch)
        darkSwitch.isChecked = prefs.getBoolean("dark_mode", false)
        darkSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            Toast.makeText(
                this,
                if (isChecked) "Dark mode coming soon" else "Light mode",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Logout
        findViewById<LinearLayout>(R.id.menuLogout).setOnClickListener {
            showLogoutConfirm()
        }
    }

    private fun loadStats() {
        RetrofitClient.apiService.getQuizzes("Bearer $token")
            .enqueue(object : Callback<List<Quiz>> {
                override fun onResponse(
                    call: Call<List<Quiz>>,
                    response: Response<List<Quiz>>
                ) {
                    if (response.isSuccessful) {
                        val quizzes = response.body() ?: emptyList()

                        // Total quizzes created
                        statQuizzes.text = quizzes.size.toString()

                        // Total questions across all quizzes as proxy for "students"
                        val totalQuestions = quizzes.sumOf { it.questionCount ?: 0 }
                        statStudents.text = when {
                            totalQuestions >= 1000 ->
                                String.format("%.1fk", totalQuestions / 1000.0)
                            else -> totalQuestions.toString()
                        }

                        // Accuracy: not available from API, show placeholder
                        statAccuracy.text = "—"
                    }
                }

                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {
                    // Keep default values on failure
                }
            })
    }

    private fun showLogoutConfirm() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { _, _ -> logout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        getSharedPreferences("MyApp", MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }
}