package com.example.mobileforquizapp.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinRoomActivity : AppCompatActivity() {

    private lateinit var roomCodeInput: TextInputEditText
    private lateinit var joinButton: MaterialButton

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)

        roomCodeInput = findViewById(R.id.roomCodeInput)
        joinButton    = findViewById(R.id.joinButton)

        token = intent.getStringExtra("jwt_token")
            ?: getSharedPreferences("MyApp", MODE_PRIVATE).getString("jwt_token", null)

        if (token == null) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        joinButton.setOnClickListener {
            val code = roomCodeInput.text.toString().trim().uppercase()
            if (code.isEmpty()) {
                Toast.makeText(this, "Enter a room code.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            joinButton.isEnabled = false
            joinButton.text = "Joining..."

            RetrofitClient.apiService.joinSession("Bearer $token", code)
                .enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(
                        call: Call<Map<String, Any>>,
                        response: Response<Map<String, Any>>
                    ) {
                        joinButton.isEnabled = true
                        joinButton.text = "Join Game"
                        if (response.isSuccessful) {
                            val body = response.body()
                            val quizId = (body?.get("quizId") as? Double)?.toLong() ?: -1L
                            val intent = Intent(this@JoinRoomActivity, LiveQuizActivity::class.java)
                            intent.putExtra("room_code", code)
                            intent.putExtra("quiz_id", quizId)
                            intent.putExtra("jwt_token", token)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@JoinRoomActivity,
                                "Room not found or not active.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        joinButton.isEnabled = true
                        joinButton.text = "Join Game"
                        Toast.makeText(this@JoinRoomActivity,
                            "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}