package com.example.QuizApp.controller;

import com.example.QuizApp.model.Question;
import com.example.QuizApp.repository.QuestionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quizzes")
public class AdminQuizController {
    private final QuestionRepository questionRepository;

    public AdminQuizController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<Question> addQuiz(@RequestBody Question question) {
        Question saved = questionRepository.save(question);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Question> getAllQuizzes() {
        return questionRepository.findAll();
    }
}
