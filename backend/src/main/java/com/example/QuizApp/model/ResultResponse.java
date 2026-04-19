package com.example.QuizApp.model;

public class ResultResponse {
    private int score;
    private String message;

    public ResultResponse(int score, String message) {
        this.score = score;
        this.message = null;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
