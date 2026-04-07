package com.example.mobileforquizapp.quiz

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mobileforquizapp.quiz.model.Quiz

class QuizPagerAdapter(
    activity: AppCompatActivity,
    private val quizzes: List<Quiz>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = quizzes.size

    override fun createFragment(position: Int): Fragment {
        val quiz = quizzes[position]
        return QuizFragment.newInstance(
            quiz.question,
            ArrayList(quiz.options),
            quiz.id ?: -1L
        )
    }
}
