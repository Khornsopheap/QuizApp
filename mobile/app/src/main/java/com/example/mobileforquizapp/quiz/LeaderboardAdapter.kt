package com.example.mobileforquizapp.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.util.AvatarUtils

class LeaderboardAdapter(
    private val entries: List<Map<String, Any>>
) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankText:     TextView  = view.findViewById(R.id.rankText)
        val avatarImage:  ImageView = view.findViewById(R.id.avatarImage)
        val usernameText: TextView  = view.findViewById(R.id.usernameText)
        val labelText:    TextView  = view.findViewById(R.id.labelText)
        val scoreText:    TextView  = view.findViewById(R.id.scoreText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]

        // Rank starts at 4 since top 3 are in podium
        holder.rankText.text = (position + 4).toString()

        val name = (entry["username"] as? String)
            ?: (entry["name"] as? String)
            ?: "?"
        holder.usernameText.text = name

        val score = when (val s = entry["score"]) {
            is Double -> s.toInt().toString()
            is Int    -> s.toString()
            is String -> s
            else      -> "0"
        }
        holder.scoreText.text = score
        holder.labelText.visibility = View.GONE

        // Load DiceBear avatar
        Glide.with(holder.itemView.context)
            .load(AvatarUtils.getAvatarUrl(name))
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_person)
            .into(holder.avatarImage)
    }

    override fun getItemCount() = entries.size
}