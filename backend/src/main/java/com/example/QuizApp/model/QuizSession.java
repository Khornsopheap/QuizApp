package com.example.QuizApp.model;

import jakarta.persistence.*;

@Entity
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomCode;
    private Long quizId;
    private boolean active;

    public QuizSession() {}

    public Long getId() { return id; }
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}