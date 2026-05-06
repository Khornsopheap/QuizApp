package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.login.LoginActivity
import com.example.mobileforquizapp.login.model.RegisterRequest
import com.example.mobileforquizapp.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var chipUser: LinearLayout
    private lateinit var chipAdmin: LinearLayout
    private lateinit var iconStudent: ImageView
    private lateinit var iconTeacher: ImageView
    private lateinit var tvStudent: TextView
    private lateinit var tvTeacher: TextView

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    private var selectedRole = "USER" // default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        chipUser    = findViewById(R.id.chipUser)
        chipAdmin   = findViewById(R.id.chipAdmin)
        iconStudent = findViewById(R.id.iconStudent)
        iconTeacher = findViewById(R.id.iconTeacher)
        tvStudent   = findViewById(R.id.tvStudent)
        tvTeacher   = findViewById(R.id.tvTeacher)

        etUsername        = findViewById(R.id.etUsername)
        etPassword        = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        // Default selection
        selectChip(chipUser)

        chipUser.setOnClickListener  { selectChip(chipUser) }
        chipAdmin.setOnClickListener { selectChip(chipAdmin) }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnRegister)
            .setOnClickListener { handleRegister() }

        findViewById<TextView>(R.id.tvGoLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun selectChip(selected: LinearLayout) {
        val isStudent     = selected == chipUser
        val activeColor   = ContextCompat.getColor(this, R.color.white)
        val inactiveColor = ContextCompat.getColor(this, R.color.quiz_text_secondary)

        // Student chip
        chipUser.setBackgroundResource(
            if (isStudent) R.drawable.chip_selected_bg else android.R.color.transparent
        )
        iconStudent.imageTintList = ColorStateList.valueOf(
            if (isStudent) activeColor else inactiveColor
        )
        tvStudent.setTextColor(if (isStudent) activeColor else inactiveColor)

        // Teacher chip
        chipAdmin.setBackgroundResource(
            if (!isStudent) R.drawable.chip_selected_bg else android.R.color.transparent
        )
        iconTeacher.imageTintList = ColorStateList.valueOf(
            if (!isStudent) activeColor else inactiveColor
        )
        tvTeacher.setTextColor(if (!isStudent) activeColor else inactiveColor)

        selectedRole = if (isStudent) "USER" else "ADMIN"
    }

    private fun handleRegister() {
        val username  = etUsername.text.toString().trim()
        val password  = etPassword.text.toString().trim()
        val confirmPw = etConfirmPassword.text.toString().trim()

        if (username.isEmpty()) {
            etUsername.error = "Name is required"
            return
        }
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            return
        }
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return
        }
        if (password != confirmPw) {
            etConfirmPassword.error = "Passwords do not match"
            return
        }

        val request = RegisterRequest(
            username = username,
            password = password,
            role     = selectedRole
        )

        RetrofitClient.apiService.register(request)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Account created! Please sign in.",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration failed (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}