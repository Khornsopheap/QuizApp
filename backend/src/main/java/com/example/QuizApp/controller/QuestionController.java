package com.example.QuizApp.controller;


import com.example.QuizApp.model.Question;
import com.example.QuizApp.model.QuizSubmission;
import com.example.QuizApp.model.ResultResponse;
import com.example.QuizApp.repository.QuestionRepository;
import com.example.QuizApp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

//    @PostMapping("/submit")
//    @PreAuthorize("hasRole('USER')")
//    public ResultResponse submitAnswers(@RequestBody QuizSubmission submission) {
//        return quizService.calculateScore(submission.getAnswers());
//    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Question createQuestion(@RequestBody Question question) {
        return questionRepository.save(question);
    }
}

