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

    public ResultResponse calculateScore(Long quizId, Map<Long, String> answers) {
        // Get all questions for this quiz
        List<Question> questions = questionRepository.findByQuizId(quizId);

        int score = 0;
        for (Question q : questions) {
            String userAnswer = answers.get(q.getId());
            if (userAnswer != null && userAnswer.equalsIgnoreCase(q.getCorrectAnswer())) {
                score += q.getScore(); // or just score++ if each question = 1 point
            }
        }

        return new ResultResponse(score, questions.size(), "Score has been calculated");
    }



    public Question findById(Long id) {
        Optional<Question> quiz = questionRepository.findById(id);
        return quiz.orElse(null); // return null if not found
    }

    public Question saveQuiz(Question question) {
        return questionRepository.save(question);
    }

}
