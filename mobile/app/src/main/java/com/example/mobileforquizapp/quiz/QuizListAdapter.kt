package com.example.mobileforquizapp.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.quiz.model.Quiz
import com.google.android.material.button.MaterialButton

class QuizListAdapter(
    private val quizzes: List<Quiz>,
    private val isAdmin: Boolean,
    private val onQuizClick: (Quiz) -> Unit
) : RecyclerView.Adapter<QuizListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView           = view.findViewById(R.id.quizTitle)
        val description: TextView     = view.findViewById(R.id.quizDescription)
        val actionButton: MaterialButton = view.findViewById(R.id.quizActionButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quiz = quizzes[position]
        holder.title.text       = quiz.title ?: "Untitled Quiz"
        holder.description.text = quiz.description ?: "No description"

        // ✅ Button label changes based on role
        holder.actionButton.text = if (isAdmin) "Manage Questions" else "View Quiz"
        holder.actionButton.setOnClickListener { onQuizClick(quiz) }
        holder.itemView.setOnClickListener { onQuizClick(quiz) }
    }

    override fun getItemCount() = quizzes.size
}