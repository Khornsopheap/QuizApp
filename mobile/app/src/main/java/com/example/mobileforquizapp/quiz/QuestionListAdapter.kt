package com.example.mobileforquizapp.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.quiz.model.Question

class QuestionListAdapter(
    private var questions: MutableList<Question>,
    private val onEdit: (Question) -> Unit,
    private val onDelete: (Question) -> Unit
) : RecyclerView.Adapter<QuestionListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.questionText)
        val optionsText: TextView = view.findViewById(R.id.optionsText)
        val correctAnswerText: TextView = view.findViewById(R.id.correctAnswerText)
        val scoreText: TextView = view.findViewById(R.id.scoreText)
        val editBtn: Button = view.findViewById(R.id.editButton)
        val deleteBtn: Button = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val q = questions[position]
        holder.questionText.text = q.question
        holder.optionsText.text = "Options: ${q.options.joinToString(", ")}"
        holder.correctAnswerText.text = "Correct Answer: ${q.correctAnswer}"
        holder.scoreText.text = "Score: ${q.score}"

        holder.editBtn.setOnClickListener { onEdit(q) }
        holder.deleteBtn.setOnClickListener { onDelete(q) }
    }

    override fun getItemCount() = questions.size
}
