package com.example.QuizApp.repository;

import com.example.QuizApp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Question, Long> {
}
