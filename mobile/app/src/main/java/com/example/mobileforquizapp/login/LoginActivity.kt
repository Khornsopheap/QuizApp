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
import com.example.mobileforquizapp.network.RetrofitClient
import com.example.mobileforquizapp.quiz.AdminDashboardActivity
import com.example.mobileforquizapp.quiz.QuizListActivity
import com.example.mobileforquizapp.quiz.RegisterActivity
import com.example.mobileforquizapp.quiz.UserDashboardActivity
import com.example.mobileforquizapp.util.AuthUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LoginActivity", "1")
        setContentView(R.layout.activity_login)
        Log.d("LoginActivity", "2")

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton   = findViewById<Button>(R.id.loginBtn)

        findViewById<TextView>(R.id.registerLink).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        Log.d("LoginActivity", "onCreate called")

        loginButton.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked")
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(username, password)

            RetrofitClient.apiService.login(user).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val token = response.body()?.token
                        Log.d("LoginActivity", "Token received: $token")

                        if (!token.isNullOrEmpty()) {

                            val role    = AuthUtils.getRoleFromToken(token)
                            val isAdmin = role == "ADMIN"
                            Log.d("LoginActivity", "Role decoded: $role")

                            val prefs: SharedPreferences =
                                getSharedPreferences("MyApp", MODE_PRIVATE)
                            prefs.edit()
                                .putString("jwt_token", token)
                                .putBoolean("is_admin", isAdmin)
                                .apply()
                            Log.d("LoginActivity", "Token and role saved")

                            if (isAdmin) {
                                val intent = Intent(
                                    this@LoginActivity,
                                    AdminDashboardActivity::class.java
                                )
                                intent.putExtra("jwt_token", token)
                                intent.putExtra("is_admin", true)
                                startActivity(intent)
                            } else {
                                val intent = Intent(
                                    this@LoginActivity,
                                    UserDashboardActivity::class.java
                                )
                                intent.putExtra("jwt_token", token)
                                intent.putExtra("is_admin", false)
                                startActivity(intent)
                            }
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