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
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class QuizListAdapter(
    private val quizzes: List<Quiz>,
    private val isAdmin: Boolean,
    private val onQuizClick: (Quiz) -> Unit
) : RecyclerView.Adapter<QuizListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView              = view.findViewById(R.id.quizTitle)
        val description: TextView        = view.findViewById(R.id.quizDescription)
        val actionButton: MaterialButton = view.findViewById(R.id.quizActionButton)
        val numberBadge: MaterialCardView = view.findViewById(R.id.numberBadge)
        val quizNumber: TextView         = view.findViewById(R.id.quizNumber)
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
            .inflate(R.layout.item_quiz, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quiz = quizzes[position]

        holder.title.text       = quiz.title ?: "Untitled Quiz"
        holder.description.text = quiz.description ?: "No description"
        holder.actionButton.text = if (isAdmin) "Manage" else "View"

        // Colored number badge
        val color = ContextCompat.getColor(
            holder.itemView.context,
            accentColors[position % accentColors.size]
        )
        holder.numberBadge.setCardBackgroundColor(
            ColorUtils.setAlphaComponent(color, 40)
        )
        holder.quizNumber.setTextColor(color)
        holder.quizNumber.text = "${position + 1}"

        holder.actionButton.setOnClickListener { onQuizClick(quiz) }
        holder.itemView.setOnClickListener { onQuizClick(quiz) }
    }

    override fun getItemCount() = quizzes.size
}