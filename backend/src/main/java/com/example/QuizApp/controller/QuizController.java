package com.example.QuizApp.controller;

import com.example.QuizApp.model.Question;
import com.example.QuizApp.model.Quiz;
import com.example.QuizApp.model.QuizSubmission;
import com.example.QuizApp.model.ResultResponse;
import com.example.QuizApp.repository.QuestionRepository;
import com.example.QuizApp.repository.QuizRepository;
import com.example.QuizApp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizController(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @Autowired
    QuizService quizService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizRepository.findAll());
    }

    @GetMapping("/{id}/questions")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Question> getQuestionsByQuizId(@PathVariable Long id) {
        return questionRepository.findByQuizId(id);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResultResponse submitQuiz(
            @PathVariable Long id,
            @RequestBody QuizSubmission submission) {
        return quizService.calculateScore(id, submission.getAnswers());
    }


}
