package com.example.mobileforquizapp.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.card.MaterialCardView

class QuizListAdapter(
    private val quizzes: List<Quiz>,
    private val isAdmin: Boolean,
    private val onQuizClick: (Quiz) -> Unit
) : RecyclerView.Adapter<QuizListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView               = view.findViewById(R.id.quizTitle)
        val description: TextView         = view.findViewById(R.id.quizDescription)
        val category: TextView            = view.findViewById(R.id.quizCategory)
        val numberBadge: MaterialCardView = view.findViewById(R.id.numberBadge)
        val quizNumber: TextView          = view.findViewById(R.id.quizNumber)
    }

    private val accentColors = listOf(
        R.color.card_accent_1,
        R.color.card_accent_2,
        R.color.card_accent_3,
        R.color.card_accent_4,
        R.color.card_accent_5,
        R.color.card_accent_6
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quiz = quizzes[position]

        // Title
        holder.title.text = quiz.title ?: "Untitled Quiz"

        // Category tag
        holder.category.text = quiz.title ?: "General"

        // Time — show duration if available, else fallback
//        holder.description.text = quiz.duration?.let { "${it} mins" } ?: "—"

        // Colored number badge
        val color = ContextCompat.getColor(
            holder.itemView.context,
            accentColors[position % accentColors.size]
        )
        holder.numberBadge.setCardBackgroundColor(
            ColorUtils.setAlphaComponent(color, 40)
        )
        holder.quizNumber.setTextColor(color)
        holder.quizNumber.text = String.format("%02d", position + 1)

        // Click listener
        holder.itemView.setOnClickListener { onQuizClick(quiz) }
    }

    override fun getItemCount() = quizzes.size
}