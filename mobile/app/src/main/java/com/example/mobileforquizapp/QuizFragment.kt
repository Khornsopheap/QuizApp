package com.example.mobileforquizapp.quiz

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobileforquizapp.R

class QuizFragment : Fragment(R.layout.fragment_quiz) {

    companion object {
        fun newInstance(question: String, options: ArrayList<String>, quizId: Long): QuizFragment {
            val fragment = QuizFragment()
            val args = Bundle()
            args.putString("question", question)
            args.putStringArrayList("options", options)
            args.putLong("quizId", quizId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val questionText = view.findViewById<TextView>(R.id.questionText)
        val optionsGroup = view.findViewById<RadioGroup>(R.id.optionsGroup)

        val question = arguments?.getString("question") ?: ""
        val options = arguments?.getStringArrayList("options") ?: arrayListOf()
        val quizId = arguments?.getLong("quizId") ?: -1L

        questionText.text = question
        optionsGroup.removeAllViews()

        options.forEach { option ->
            val radioButton = RadioButton(requireContext()).apply {
                text = option
                id = View.generateViewId()
            }
            optionsGroup.addView(radioButton)
        }

        optionsGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedButton = group.findViewById<RadioButton>(checkedId)
            (activity as? QuizActivity)?.saveAnswer(quizId, selectedButton.text.toString())
            (activity as? QuizActivity)?.goToNextQuestion()
        }
    }
}
