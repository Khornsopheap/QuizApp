package com.example.mobileforquizapp.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.quiz.model.Question
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class QuestionListAdapter(
    private val questions: MutableList<Question>,
    private val onEdit: (Question) -> Unit,
    private val onDelete: (Question) -> Unit
) : RecyclerView.Adapter<QuestionListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView     = view.findViewById(R.id.questionText)
        val option1Text: TextView      = view.findViewById(R.id.option1Text)
        val option2Text: TextView      = view.findViewById(R.id.option2Text)
        val option3Text: TextView      = view.findViewById(R.id.option3Text)
        val option4Text: TextView      = view.findViewById(R.id.option4Text)
        val correctAnswerChip: Chip    = view.findViewById(R.id.correctAnswerChip)
        val scoreChip: Chip            = view.findViewById(R.id.scoreChip)
        val editButton: MaterialButton = view.findViewById(R.id.editButton)
        val deleteButton: MaterialButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]
        val options = question.options

        // Add this line in onBindViewHolder
        holder.questionText.text = "${position + 1}. ${question.question}"
        holder.option1Text.text     = "A. ${options.getOrNull(0) ?: "—"}"
        holder.option2Text.text     = "B. ${options.getOrNull(1) ?: "—"}"
        holder.option3Text.text     = "C. ${options.getOrNull(2) ?: "—"}"
        holder.option4Text.text     = "D. ${options.getOrNull(3) ?: "—"}"
        holder.correctAnswerChip.text = "✓ ${question.correctAnswer}"
        holder.scoreChip.text         = "${question.score} pts"

        holder.editButton.setOnClickListener {
            onEdit(question)
        }

        holder.deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(holder.itemView.context)
                .setTitle("Delete Question")
                .setMessage("Are you sure you want to delete this question? This cannot be undone.")
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Delete") { _, _ -> onDelete(question) }
                .show()
        }
    }

    override fun getItemCount() = questions.size
}