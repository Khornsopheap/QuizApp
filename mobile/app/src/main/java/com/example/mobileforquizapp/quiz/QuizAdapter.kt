package com.example.mobileforquizapp.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.quiz.model.Question

class QuizAdapter(
    private val questions: List<Question>,
    private val onItemClick: (Question) -> Unit
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    private val userAnswers = MutableList(questions.size) { "" }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_management, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = questions[position]
        holder.bind(quiz, position)
    }

    override fun getItemCount(): Int = questions.size

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val optionsGroup: RadioGroup = itemView.findViewById(R.id.optionsGroup)

        fun bind(question: Question, position: Int) {
            questionText.text = question.question
            optionsGroup.removeAllViews()

            val safeOptions = question.options ?: emptyList()
            safeOptions.forEach { option ->
                val radioButton = RadioButton(itemView.context).apply {
                    text = option
                    id = View.generateViewId()
                }
                optionsGroup.addView(radioButton)

                if (userAnswers[position] == option) {
                    radioButton.isChecked = true
                }
            }

            optionsGroup.setOnCheckedChangeListener { group, checkedId ->
                val selectedButton = group.findViewById<RadioButton>(checkedId)
                userAnswers[position] = selectedButton.text.toString()
            }

            itemView.setOnClickListener {
                onItemClick(question)
            }
        }

    }

    fun getSelectedAnswersMap(): Map<Long, String> {
        val answersMap = mutableMapOf<Long, String>()
        questions.forEachIndexed { index, quiz ->
            val selected = userAnswers[index]
            if (selected.isNotEmpty() && quiz.id != null) {
                answersMap[quiz.id!!] = selected
            }
        }
        return answersMap
    }
}
