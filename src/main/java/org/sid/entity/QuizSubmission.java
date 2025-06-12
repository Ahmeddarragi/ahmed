package org.sid.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_submissions")
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "total_questions")
    private int totalQuestions;
    
    @Column(name = "correct_answers")
    private int correctAnswers;
    
    @Column(name = "score_percentage")
    private double scorePercentage;
    
    @Column(name = "score")
    private int score;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "submission_date")
    private LocalDateTime submissionDate;
    
    // Default constructor
    public QuizSubmission() {
        this.submissionDate = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public QuizSubmission(int totalQuestions, int correctAnswers, double scorePercentage, int score, String email) {
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.scorePercentage = scorePercentage;
        this.score = score;
        this.email = email;
        this.submissionDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    
    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
    
    public double getScorePercentage() {
        return scorePercentage;
    }
    
    public void setScorePercentage(double scorePercentage) {
        this.scorePercentage = scorePercentage;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    @Override
    public String toString() {
        return "QuizSubmission{" +
                "id=" + id +
                ", totalQuestions=" + totalQuestions +
                ", correctAnswers=" + correctAnswers +
                ", scorePercentage=" + scorePercentage +
                ", score=" + score +
                ", email='" + email + '\'' +
                ", submissionDate=" + submissionDate +
                '}';
    }
}