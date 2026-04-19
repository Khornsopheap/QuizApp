package com.example.QuizApp.service;

import com.example.QuizApp.model.Question;
import com.example.QuizApp.model.ResultResponse;
import com.example.QuizApp.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getAllQuizzes() {
        return questionRepository.findAll();
    }

    public ResultResponse calculateScore(Map<Long, String> answers) {
        int total = 0;

        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Long quizId = entry.getKey();
            String userAnswer = entry.getValue();

            Question question = questionRepository.findById(quizId).orElse(null);
            if (question != null) {
                if (question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                    total += question.getScore();
                }
            }
        }

        return new ResultResponse(total, "Score has been calculated");
    }


    public Question findById(Long id) {
        Optional<Question> quiz = questionRepository.findById(id);
        return quiz.orElse(null); // return null if not found
    }

    public Question saveQuiz(Question question) {
        return questionRepository.save(question);
    }

}
