package com.example.mobileforquizapp.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.login.model.LoginResponse
import com.example.mobileforquizapp.login.model.User
import com.example.mobileforquizapp.network.JwtUtils
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.AdminDashboardActivity
import com.example.mobileforquizapp.quiz.RegisterActivity
import com.example.mobileforquizapp.quiz.UserDashboardActivity
import com.example.mobileforquizapp.util.AuthUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton   = findViewById<Button>(R.id.loginBtn)

        findViewById<TextView>(R.id.registerLink).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.apiService.login(User(username, password))
                .enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val token = response.body()?.token

                            if (!token.isNullOrEmpty()) {
                                val role         = AuthUtils.getRoleFromToken(token)
                                val isAdmin      = role == "ADMIN"
                                // Extract username from JWT token
                                val decodedName  = JwtUtils.getUsername(token) ?: username

                                Log.d("LoginActivity", "Role: $role | Username: $decodedName")

                                // Save everything to SharedPreferences
                                getSharedPreferences("MyApp", MODE_PRIVATE)
                                    .edit()
                                    .putString("jwt_token", token)
                                    .putString("username", decodedName)
                                    .putString("role", role)
                                    .putBoolean("is_admin", isAdmin)
                                    .apply()

                                // Navigate to correct dashboard
                                val destination = if (isAdmin) {
                                    AdminDashboardActivity::class.java
                                } else {
                                    UserDashboardActivity::class.java
                                }

                                startActivity(
                                    Intent(this@LoginActivity, destination).apply {
                                        putExtra("jwt_token", token)
                                        putExtra("is_admin", isAdmin)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                )
                                finish()

                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Invalid credentials",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login failed: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("LoginActivity", "Network error", t)
                    }
                })
        }
    }
}