package com.example.QuizApp;


import com.example.QuizApp.model.Quiz;
import com.example.QuizApp.model.QuizSubmission;
import com.example.QuizApp.model.ResultResponse;
import com.example.QuizApp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    @Autowired
    private QuizService quizService;

    // GET /api/quizzes
    @GetMapping
    public ResponseEntity<List<Quiz>> getQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    @PostMapping("/submit")
    public ResponseEntity<ResultResponse> submitQuiz(@RequestBody QuizSubmission submission) {
        ResultResponse result = quizService.evaluateQuiz(submission);
        return ResponseEntity.ok(result);
    }

}
