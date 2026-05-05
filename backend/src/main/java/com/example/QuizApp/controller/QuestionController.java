package com.example.QuizApp.controller;

import com.example.QuizApp.model.Question;
import com.example.QuizApp.model.Quiz;
import com.example.QuizApp.repository.QuestionRepository;
import com.example.QuizApp.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    @PostMapping("/quiz/{quizId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createQuestion(
            @PathVariable Long quizId,
            @RequestBody Question question) {

        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return ResponseEntity.badRequest().body("Quiz not found: " + quizId);
        }

        question.setQuiz(quiz);
        return ResponseEntity.ok(questionRepository.save(question));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long id,
            @RequestBody Question updated) {

        return questionRepository.findById(id).map(existing -> {
            existing.setQuestion(updated.getQuestion());
            existing.setOptions(updated.getOptions());
            existing.setCorrectAnswer(updated.getCorrectAnswer());
            existing.setScore(updated.getScore());
            return ResponseEntity.ok(questionRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
