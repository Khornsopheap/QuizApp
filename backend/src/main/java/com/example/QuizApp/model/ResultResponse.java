package com.example.QuizApp.model;

public class ResultResponse {
    private int score;
    private int total;
    private String message;

    public ResultResponse(int score, int total, String message) {
        this.score = score;
        this.total = total;
        this.message = message;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
