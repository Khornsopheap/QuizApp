package com.example.QuizApp.service;

import com.example.QuizApp.model.Quiz;
import com.example.QuizApp.model.QuizSubmission;
import com.example.QuizApp.model.ResultResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class QuizService {
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        quizzes.add(new Quiz(1L, "What is 2+2?", Arrays.asList("3","4","5"), "4"));
        quizzes.add(new Quiz(2L, "Capital of France?", Arrays.asList("Berlin","Paris","London"), "Paris"));

        return quizzes;
    }

    public ResultResponse evaluateQuiz(QuizSubmission submission) {
        // Dummy evaluation logic
        int score = 0;
        if (submission.getAnswers().containsValue("4")) score++;
        if (submission.getAnswers().containsValue("Paris")) score++;
        return new ResultResponse(score, "Quiz evaluated successfully");
    }
}
