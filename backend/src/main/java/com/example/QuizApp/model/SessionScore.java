package com.example.QuizApp.model;

import jakarta.persistence.*;

@Entity
public class SessionScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomCode;
    private String username;
    private int score;
    private boolean finished;  // true = user submitted all answers

    public SessionScore() {}

    public Long getId() { return id; }
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }
}