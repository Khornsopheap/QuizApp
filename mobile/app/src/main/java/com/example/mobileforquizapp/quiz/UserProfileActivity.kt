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

class UserProfileActivity : UserBaseActivity() {

    override fun currentNavItem() = NAV_PROFILE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupNav()

        val prefs    = getSharedPreferences("MyApp", MODE_PRIVATE)
        val username = prefs.getString("username", "Student") ?: "Student"

        findViewById<TextView>(R.id.profileName)?.text      = username
        findViewById<TextView>(R.id.profileRoleLabel)?.text = "STUDENT"

        loadStats()

        findViewById<LinearLayout>(R.id.menuPersonalInfo).setOnClickListener {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.menuActivityHistory).setOnClickListener {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.menuNotifications).setOnClickListener {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.menuSupport).setOnClickListener {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<SwitchMaterial>(R.id.darkModeSwitch).setOnCheckedChangeListener { _, _ ->
            Toast.makeText(this, "Dark mode coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.menuLogout).setOnClickListener {
            logout()
        }
    }

    private fun loadStats() {
        RetrofitClient.apiService.getQuizzes("Bearer $token")
            .enqueue(object : Callback<List<Quiz>> {
                override fun onResponse(call: Call<List<Quiz>>, response: Response<List<Quiz>>) {
                    if (response.isSuccessful) {
                        val count = response.body()?.size ?: 0
                        findViewById<TextView>(R.id.statQuizzes)?.text  = count.toString()
                        findViewById<TextView>(R.id.statStudents)?.text = "—"
                        findViewById<TextView>(R.id.statAccuracy)?.text = "—"
                    }
                }
                override fun onFailure(call: Call<List<Quiz>>, t: Throwable) {}
            })
    }

    private fun logout() {
        getSharedPreferences("MyApp", MODE_PRIVATE).edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}