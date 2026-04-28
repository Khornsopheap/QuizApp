package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.login.model.RegisterRequest
import com.example.mobileforquizapp.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvGoLogin: android.widget.TextView
    private lateinit var chipUser: com.google.android.material.chip.Chip
    private lateinit var chipAdmin: com.google.android.material.chip.Chip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUsername        = findViewById(R.id.etUsername)
        etPassword        = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister       = findViewById(R.id.btnRegister)
        tvGoLogin         = findViewById(R.id.tvGoLogin)
        chipUser  = findViewById(R.id.chipUser)
        chipAdmin = findViewById(R.id.chipAdmin)

        tvGoLogin.setOnClickListener { finish() }

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm  = etConfirmPassword.text.toString().trim()
            val role     = if (chipAdmin.isChecked) "ADMIN" else "USER"

            // Validation
            if (username.isEmpty()) {
                etUsername.error = "Username is required"
                return@setOnClickListener
            }
            if (username.length < 3) {
                etUsername.error = "Username must be at least 3 characters"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = "Password is required"
                return@setOnClickListener
            }
            if (password.length < 6) {
                etPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }
            if (password != confirm) {
                etConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            btnRegister.isEnabled = false

            val request = RegisterRequest(
                username = username,
                password = password,
                role     = role
            )

            RetrofitClient.apiService.register(request)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        btnRegister.isEnabled = true
                        when (response.code()) {
                            201 -> {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Account created as $role! Please sign in.",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                            409 -> {
                                etUsername.error = "Username already taken"
                            }
                            else -> {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Registration failed (${response.code()})",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        btnRegister.isEnabled = true
                        Toast.makeText(
                            this@RegisterActivity,
                            "Network error: ${t.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}