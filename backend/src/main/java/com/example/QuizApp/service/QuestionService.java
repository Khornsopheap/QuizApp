package com.example.QuizApp.service;

import com.example.QuizApp.model.Question;
import com.example.QuizApp.model.Quiz;
import com.example.QuizApp.repository.QuestionRepository;
import com.example.QuizApp.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private QuizRepository quizRepo;

    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepo.findByQuizId(quizId);
    }

    public Question updateQuestion(Long id, Question updated) {
        Question q = questionRepo.findById(id).orElseThrow();

        q.setQuestion(updated.getQuestion());
        q.setOptions(updated.getOptions());
        q.setCorrectAnswer(updated.getCorrectAnswer());
        q.setScore(updated.getScore());
        q.setCategory(updated.getCategory());

        if (updated.getId() != null) {
            Quiz quiz = quizRepo.findById(updated.getId()).orElseThrow();
            q.setQuiz(quiz);
        }

        return questionRepo.save(q);
    }

    public void deleteQuestion(Long id) {
        questionRepo.deleteById(id);
    }
}
