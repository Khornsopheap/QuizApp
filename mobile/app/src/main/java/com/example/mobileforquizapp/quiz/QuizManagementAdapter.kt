package com.example.mobileforquizapp.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class QuizManagementAdapter(
    private var quizzes: List<Quiz>,
    private val onStartClick:  (Quiz) -> Unit,
    private val onEditClick:   (Quiz) -> Unit,
    private val onDeleteClick: (Quiz) -> Unit
) : RecyclerView.Adapter<QuizManagementAdapter.ViewHolder>() {


    // Keywords in quiz title → specific background image
    private val keywordImageMap = mapOf(
        listOf("math", "algebra", "calculus", "geometry", "number")
                to R.drawable.quiz_bg_1,
        listOf("science", "biology", "chemistry", "physics", "space", "lab")
                to R.drawable.quiz_bg_2,
        listOf("history", "geography", "social", "world", "war", "culture")
                to R.drawable.quiz_bg_3,
        listOf("tech", "computer", "programming", "code", "software", "it")
                to R.drawable.quiz_bg_4,
    )

    // Rotate through all 6 images when no keyword matches
    private val fallbackImages = listOf(
        R.drawable.quiz_bg_1,
        R.drawable.quiz_bg_2,
        R.drawable.quiz_bg_3,
        R.drawable.quiz_bg_4
    )

    private fun getImageFor(quiz: Quiz, position: Int): Int {
        val titleLower = quiz.title.lowercase()
        for ((keywords, resId) in keywordImageMap) {
            if (keywords.any { titleLower.contains(it) }) return resId
        }
        return fallbackImages[position % fallbackImages.size]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quizImage:         ImageView        = view.findViewById(R.id.quizImage)
        val quizCategory:      TextView         = view.findViewById(R.id.quizCategory)
        val quizStatus:        TextView         = view.findViewById(R.id.quizStatus)
        val quizTitle:         TextView         = view.findViewById(R.id.quizTitle)
        val quizQuestionCount: TextView         = view.findViewById(R.id.quizQuestionCount)
        val quizDuration:      TextView         = view.findViewById(R.id.quizDuration)
        val btnStart:          MaterialButton   = view.findViewById(R.id.btnStart)
        val btnEdit:           MaterialCardView = view.findViewById(R.id.btnEdit)
        val btnDelete:         MaterialCardView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_management, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quiz = quizzes[position]

        holder.quizImage.setImageResource(getImageFor(quiz, position))
        holder.quizTitle.text         = quiz.title
        holder.quizQuestionCount.text = "${quiz.questionCount ?: 0} Questions"
        holder.quizDuration.text      = "${(quiz.questionCount ?: 0) * 2} Mins"

        // Use first word of description as category badge, fallback to GENERAL
        holder.quizCategory.text = quiz.description
            .trim()
            .split(" ")
            .firstOrNull()
            ?.uppercase()
            ?.take(12)
            ?: "GENERAL"

        holder.quizStatus.text = "Published"

        holder.btnStart.setOnClickListener  { onStartClick(quiz)  }
        holder.btnEdit.setOnClickListener   { onEditClick(quiz)   }
        holder.btnDelete.setOnClickListener { onDeleteClick(quiz) }
    }

    override fun getItemCount() = quizzes.size

    fun updateList(newQuizzes: List<Quiz>) {
        quizzes = newQuizzes
        notifyDataSetChanged()
    }
}