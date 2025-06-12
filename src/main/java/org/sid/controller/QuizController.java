package org.sid.controller;

import org.sid.entity.CV;
import org.sid.entity.Question;
import org.sid.entity.QuizSubmission;
import org.sid.repository.CVRepository;
import org.sid.service.EmailService;
import org.sid.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/quiz")
public class QuizController {
    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    @Autowired
    private QuizService quizService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CVRepository cvRepository;

    @GetMapping("/questions/random")
    public List<Question> getRandomQuestions() {
        logger.info("Fetching random questions");
        List<Question> questions = quizService.getRandomQuestions().stream()
                .map(question -> {
                    question.setCorrectAnswer(null);
                    return question;
                })
                .collect(Collectors.toList());
        logger.info("Returning {} random questions", questions.size());
        return questions;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody Map<String, Object> payload) {
        try {
            // Log the entire payload for debugging
            logger.info("Received quiz submission payload: {}", payload);
            
            // Extract email from payload and validate
            String email = (String) payload.get("email");
            logger.info("Extracted email from payload: {}", email);
            
            if (email == null || email.trim().isEmpty()) {
                logger.error("Email is null or empty in the request payload");
                return ResponseEntity.badRequest().body(Map.of("error", "Email cannot be empty"));
            }
            
            // Extract answers from payload
            Map<String, String> answers = (Map<String, String>) payload.get("answers");
            logger.info("Extracted answers: {}", answers);
            
            if (answers == null || answers.isEmpty()) {
                logger.error("Answers are null or empty in the request payload");
                return ResponseEntity.badRequest().body(Map.of("error", "Answers cannot be empty"));
            }

            // Convert answer keys to Long
            Map<Long, String> convertedAnswers;
            try {
                convertedAnswers = answers.entrySet().stream()
                    .map(entry -> {
                        try {
                            Long questionId = Long.parseLong(entry.getKey());
                            return Map.entry(questionId, entry.getValue());
                        } catch (NumberFormatException e) {
                            logger.error("Invalid question ID: {}", entry.getKey(), e);
                            throw new IllegalArgumentException("Identifiant de question invalide: " + entry.getKey());
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            } catch (Exception e) {
                logger.error("Error converting answers", e);
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid answer format: " + e.getMessage()));
            }

            // Submit quiz and get submission
            QuizSubmission submission;
            try {
                submission = quizService.submitQuiz(convertedAnswers, email);
                logger.info("Quiz submitted successfully for email: {}, submission ID: {}", email, submission.getId());
            } catch (Exception e) {
                logger.error("Error submitting quiz", e);
                return ResponseEntity.internalServerError().body(Map.of("error", "Error submitting quiz: " + e.getMessage()));
            }

            // Find CV by email
            CV cv = cvRepository.findByEmail(email);
            String nom = cv != null ? cv.getPrenom() + " " + cv.getNom() : "Candidat inconnu";
            logger.info("Found CV for email {}: {}", email, nom);

            // Send confirmation email
            try {
                String subject = "Confirmation de réception de votre candidature";
                String message = "Cher(e) " + nom + ",\n\n"
                        + "Merci d'avoir soumis votre candidature pour le poste d'Ingénieur Informatique.\n\n"
                        + "Nous examinerons votre candidature et vous contacterons dans les 5 jours.\n\n"
                        + "En attendant, n'hésitez pas à découvrir Vermeg et sa culture d'entreprise sur notre site https://www.vermeg.com et sur notre page LinkedIn.\n\n"
                        + "Cordialement,\n\n"
                        + "L'équipe Talent Acquisition";

                emailService.sendEmail(email, subject, message);
                logger.info("Confirmation email sent to {}", email);
            } catch (Exception e) {
                logger.error("Error sending confirmation email", e);
                // Continue even if email fails
            }

            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            logger.error("Unexpected error in submitQuiz", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/scores")
    public ResponseEntity<List<QuizSubmission>> getAllScores() {
        List<QuizSubmission> submissions = quizService.getAllSubmissions();
        logger.info("Retrieved {} quiz submissions", submissions.size());
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/emails-scores")
    public ResponseEntity<List<Map<String, Object>>> getEmailsAndScores() {
        List<Map<String, Object>> emailScores = quizService.getAllEmailScores();
        logger.info("Retrieved {} email scores", emailScores.size());
        return ResponseEntity.ok(emailScores);
    }
}