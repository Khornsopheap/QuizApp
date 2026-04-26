package com.example.mobileforquizapp.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R

class LeaderboardAdapter(
    private val entries: List<Map<String, Any>>
) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankText: TextView     = view.findViewById(R.id.rankText)
        val usernameText: TextView = view.findViewById(R.id.usernameText)
        val scoreText: TextView    = view.findViewById(R.id.scoreText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        val rank = position + 1

        // ✅ medal for top 3
        holder.rankText.text = when (rank) {
            1 -> "🥇"
            2 -> "🥈"
            3 -> "🥉"
            else -> "$rank"
        }

        holder.usernameText.text = entry["username"]?.toString() ?: "Unknown"
        val score = (entry["score"] as? Double)?.toInt() ?: 0
        holder.scoreText.text = "$score pts"
    }

    override fun getItemCount() = entries.size
}