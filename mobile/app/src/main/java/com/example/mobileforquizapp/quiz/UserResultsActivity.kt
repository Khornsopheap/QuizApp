package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R

class UserResultsActivity : UserBaseActivity() {

    override fun currentNavItem() = NAV_RESULTS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_results)

        setupNav()

        val recyclerView = findViewById<RecyclerView>(R.id.resultsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // TODO: load results from API when endpoint is available
        // For now shows empty state
        findViewById<TextView>(R.id.tvGamesPlayed).text = "0"
        findViewById<TextView>(R.id.tvTotalScore).text  = "0"
    }
}