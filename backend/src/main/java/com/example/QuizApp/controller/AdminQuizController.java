package com.example.QuizApp.controller;

import com.example.QuizApp.model.Quiz;
import com.example.QuizApp.repository.QuizRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quizzes")
public class AdminQuizController {
    private final QuizRepository quizRepository;

    public AdminQuizController(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<Quiz> addQuiz(@RequestBody Quiz quiz) {
        Quiz saved = quizRepository.save(quiz);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
}
