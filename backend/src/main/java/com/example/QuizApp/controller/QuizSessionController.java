package com.example.QuizApp.controller;

import com.example.QuizApp.model.*;
import com.example.QuizApp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/session")
public class QuizSessionController {

    @Autowired private QuizSessionRepository sessionRepo;
    @Autowired private SessionScoreRepository scoreRepo;
    @Autowired private QuizRepository quizRepo;
    @Autowired private SimpMessagingTemplate messaging;

    // ✅ Admin: create a room for a quiz
    @PostMapping("/create/{quizId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSession(@PathVariable Long quizId) {
        String roomCode = generateRoomCode();

        QuizSession session = new QuizSession();
        session.setRoomCode(roomCode);
        session.setQuizId(quizId);
        session.setActive(true);
        sessionRepo.save(session);

        return ResponseEntity.ok(Map.of("roomCode", roomCode));
    }

    // ✅ User: join a room by code
    @PostMapping("/join/{roomCode}")
    public ResponseEntity<?> joinSession(
            @PathVariable String roomCode,
            @RequestHeader("Authorization") String token) {

        QuizSession session = sessionRepo.findByRoomCode(roomCode).orElse(null);
        if (session == null || !session.isActive()) {
            return ResponseEntity.badRequest().body("Room not found or not active");
        }

        // Get username from token
        String username = com.example.QuizApp.security.JwtUtil
                .extractUsername(token.replace("Bearer ", ""));

        // Don't add duplicate
        if (!scoreRepo.existsByRoomCodeAndUsername(roomCode, username)) {
            SessionScore score = new SessionScore();
            score.setRoomCode(roomCode);
            score.setUsername(username);
            score.setScore(0);
            score.setFinished(false);
            scoreRepo.save(score);
        }

        // Notify everyone a new user joined
        messaging.convertAndSend("/topic/room/" + roomCode,
                (Object) Map.of("event", "USER_JOINED", "username", username));

        return ResponseEntity.ok(Map.of(
                "quizId", session.getQuizId(),
                "roomCode", roomCode
        ));
    }

    // ✅ User: submit their final score
    @PostMapping("/submit/{roomCode}")
    public ResponseEntity<?> submitScore(
            @PathVariable String roomCode,
            @RequestBody Map<String, Object> body,
            @RequestHeader("Authorization") String token) {

        String username = com.example.QuizApp.security.JwtUtil
                .extractUsername(token.replace("Bearer ", ""));

        int score = (int) body.get("score");

        // Update score
        List<SessionScore> scores = scoreRepo.findByRoomCodeOrderByScoreDesc(roomCode);
        scores.stream()
                .filter(s -> s.getUsername().equals(username))
                .findFirst()
                .ifPresent(s -> {
                    s.setScore(score);
                    s.setFinished(true);
                    scoreRepo.save(s);
                });

        // Broadcast updated leaderboard to everyone in the room
        List<SessionScore> updated = scoreRepo.findByRoomCodeOrderByScoreDesc(roomCode);

        messaging.convertAndSend("/topic/room/" + roomCode,
                (Object) Map.of("event", "LEADERBOARD_UPDATE", "leaderboard", username));

        return ResponseEntity.ok("Score submitted");
    }

    // ✅ Get current leaderboard
    @GetMapping("/leaderboard/{roomCode}")
    public ResponseEntity<?> getLeaderboard(@PathVariable String roomCode) {
        List<SessionScore> scores = scoreRepo.findByRoomCodeOrderByScoreDesc(roomCode);
        return ResponseEntity.ok(scores);
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}