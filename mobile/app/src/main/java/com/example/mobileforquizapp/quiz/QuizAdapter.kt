package com.example.mobileforquizapp.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileforquizapp.R
import com.example.mobileforquizapp.quiz.model.Quiz

class QuizAdapter(
    val quizzes: List<Quiz>
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    // Track selected answers for each quiz
    val userAnswers = MutableList(quizzes.size) { "" }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = quizzes[position]
        holder.bind(quiz, position)
    }

    override fun getItemCount(): Int = quizzes.size

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val optionsGroup: RadioGroup = itemView.findViewById(R.id.optionsGroup)

        fun bind(quiz: Quiz, position: Int) {
            questionText.text = quiz.question
            optionsGroup.removeAllViews()

            quiz.options.forEach { option ->
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
        }
    }

    fun getSelectedAnswersMap(): Map<Long, String> {
        val answersMap = mutableMapOf<Long, String>()
        quizzes.forEachIndexed { index, quiz ->
            val selected = userAnswers[index]
            if (selected.isNotEmpty() && quiz.id != null) {
                answersMap[quiz.id!!] = selected
            }
        }
        return answersMap
    }

}
