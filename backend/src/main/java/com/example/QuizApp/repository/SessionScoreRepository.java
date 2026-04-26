package com.example.QuizApp.repository;

import com.example.QuizApp.model.SessionScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionScoreRepository extends JpaRepository<SessionScore, Long> {
    List<SessionScore> findByRoomCodeOrderByScoreDesc(String roomCode);
    boolean existsByRoomCodeAndUsername(String roomCode, String username);
}
