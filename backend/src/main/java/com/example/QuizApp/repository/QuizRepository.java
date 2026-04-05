package com.example.QuizApp.repository;

import com.example.QuizApp.model.Quiz;
import com.example.QuizApp.model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
