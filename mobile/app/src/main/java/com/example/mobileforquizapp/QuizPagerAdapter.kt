package com.example.mobileforquizapp.quiz

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mobileforquizapp.quiz.model.Question

class QuizPagerAdapter(
    activity: AppCompatActivity,
    private val questions: List<Question>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = questions.size

    override fun createFragment(position: Int): Fragment {
        val quiz = questions[position]
        return QuizFragment.newInstance(
            quiz.question,
            ArrayList(quiz.options),
            quiz.id ?: -1L
        )
    }
}
