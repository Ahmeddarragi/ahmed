package org.sid.service;

import org.sid.entity.CV;
import org.sid.entity.Question;
import org.sid.entity.QuizSubmission;
import org.sid.repository.CVRepository;
import org.sid.repository.QuestionRepository;
import org.sid.repository.QuizSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private CVRepository cvRepository;
    
    @Autowired
    private DataSource dataSource;

    public List<Question> getRandomQuestions() {
        List<Question> allQuestions = questionRepository.findAll();
        logger.info("Found {} questions in total", allQuestions.size());
        
        if (allQuestions.isEmpty()) {
            logger.warn("No questions found in the database");
            return Collections.emptyList();
        }
        
        Collections.shuffle(allQuestions);
        List<Question> selectedQuestions = allQuestions.stream().limit(7).collect(Collectors.toList());
        logger.info("Selected {} random questions", selectedQuestions.size());
        return selectedQuestions;
    }

    @Transactional
    public QuizSubmission submitQuiz(Map<Long, String> userAnswers, String email) {
        // Log the email being used for submission
        logger.info("Submitting quiz for email: {}", email);
        
        // Validate email is not null or empty
        if (email == null || email.trim().isEmpty()) {
            logger.error("Email is null or empty");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        // Validate answers
        if (userAnswers == null || userAnswers.isEmpty()) {
            logger.error("User answers are null or empty");
            throw new IllegalArgumentException("User answers cannot be null or empty");
        }
        
        int correctAnswers = 0;
        int totalQuestions = userAnswers.size();
        logger.info("Processing {} answers", totalQuestions);

        for (Map.Entry<Long, String> entry : userAnswers.entrySet()) {
            Long questionId = entry.getKey();
            String userAnswer = entry.getValue();
            
            // Log each answer for debugging
            logger.debug("Processing answer for question ID {}: '{}'", questionId, userAnswer);
            
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> {
                        logger.error("Question not found with ID: {}", questionId);
                        return new RuntimeException("Question non trouvée avec ID: " + questionId);
                    });

            if (question.getCorrectAnswer() != null && question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                correctAnswers++;
                logger.debug("Correct answer for question ID {}", questionId);
            }
        }

        double rawScorePercentage = ((double) correctAnswers / totalQuestions) * 100;
        // Arrondi à 2 chiffres après la virgule
        BigDecimal bd = new BigDecimal(rawScorePercentage).setScale(2, RoundingMode.HALF_UP);
        double scorePercentage = bd.doubleValue();

        int score = correctAnswers;
        logger.info("Quiz score calculated: {}/{} correct, {}%", correctAnswers, totalQuestions, scorePercentage);

        // Try to save using direct JDBC first
        Long submissionId = saveSubmissionWithJdbc(totalQuestions, correctAnswers, scorePercentage, score, email);
        
        if (submissionId != null) {
            logger.info("Successfully saved submission with JDBC, ID: {}", submissionId);
            
            // Create a QuizSubmission object with the saved ID
            QuizSubmission submission = new QuizSubmission(totalQuestions, correctAnswers, scorePercentage, score, email);
            submission.setId(submissionId);
            
            try {
                CV cv = cvRepository.findByEmail(email);
                String nomPrenom = (cv != null) ? cv.getPrenom() + " " + cv.getNom() : "Candidat inconnu";

                notificationService.sendNotification(
                    "Un nouveau candidat (" + nomPrenom + " - " + email + ") a soumis son CV et obtenu une note de " 
                    + score + " (" + scorePercentage + "%) au quiz."
                );
                logger.info("Notification sent for quiz submission");
            } catch (Exception e) {
                logger.error("Error sending notification", e);
                // Continue even if notification fails
            }
            
            return submission;
        }
        
        // If JDBC fails, try with JPA
        logger.info("JDBC save failed, trying with JPA");
        
        try {
            // Create a new QuizSubmission object for each submission
            QuizSubmission submission = new QuizSubmission(totalQuestions, correctAnswers, scorePercentage, score, email);
            logger.info("Created quiz submission: {}", submission);
            
            // Save the submission to the database
            QuizSubmission savedSubmission = quizSubmissionRepository.save(submission);
            logger.info("Saved quiz submission with JPA, ID: {}", savedSubmission.getId());
            
            try {
                CV cv = cvRepository.findByEmail(email);
                String nomPrenom = (cv != null) ? cv.getPrenom() + " " + cv.getNom() : "Candidat inconnu";

                notificationService.sendNotification(
                    "Un nouveau candidat (" + nomPrenom + " - " + email + ") a soumis son CV et obtenu une note de " 
                    + score + " (" + scorePercentage + "%) au quiz."
                );
                logger.info("Notification sent for quiz submission");
            } catch (Exception e) {
                logger.error("Error sending notification", e);
                // Continue even if notification fails
            }

            return savedSubmission;
        } catch (Exception e) {
            logger.error("Error saving quiz submission with JPA", e);
            throw new RuntimeException("Erreur lors de l'enregistrement de la soumission du quiz: " + e.getMessage(), e);
        }
    }
    
    private Long saveSubmissionWithJdbc(int totalQuestions, int correctAnswers, double scorePercentage, int score, String email) {
        String sql = "INSERT INTO quiz_submissions (total_questions, correct_answers, score_percentage, score, email, submission_date) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, totalQuestions);
            pstmt.setInt(2, correctAnswers);
            pstmt.setDouble(3, scorePercentage);
            pstmt.setInt(4, score);
            pstmt.setString(5, email);
            pstmt.setObject(6, LocalDateTime.now());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                logger.error("Creating quiz submission failed, no rows affected.");
                return null;
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    logger.info("Quiz submission saved with ID: {}", id);
                    return id;
                } else {
                    logger.error("Creating quiz submission failed, no ID obtained.");
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("Error saving quiz submission with JDBC", e);
            return null;
        }
    }

    public List<QuizSubmission> getAllSubmissions() {
        try {
            List<QuizSubmission> submissions = quizSubmissionRepository.findAll();
            logger.info("Retrieved {} quiz submissions", submissions.size());
            return submissions;
        } catch (Exception e) {
            logger.error("Error retrieving quiz submissions", e);
            return Collections.emptyList();
        }
    }

    // Retourner une liste des emails avec leur score %
    public List<Map<String, Object>> getAllEmailScores() {
        try {
            List<QuizSubmission> submissions = quizSubmissionRepository.findAll();
            logger.info("Retrieved {} quiz submissions for email scores", submissions.size());
            
            return submissions.stream()
                    .map(submission -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("email", submission.getEmail());
                        result.put("scorePercentage", submission.getScorePercentage());
                        return result;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving email scores", e);
            return Collections.emptyList();
        }
    }
}