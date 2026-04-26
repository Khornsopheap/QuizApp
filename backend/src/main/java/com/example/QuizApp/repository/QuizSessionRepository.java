package com.example.QuizApp.repository;

import com.example.QuizApp.model.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {
    Optional<QuizSession> findByRoomCode(String roomCode);

}
