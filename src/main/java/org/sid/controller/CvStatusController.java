package org.sid.controller;

import org.sid.service.CvStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/cv")
public class CvStatusController {
    
    private static final Logger logger = LoggerFactory.getLogger(CvStatusController.class);

    @Autowired
    private CvStatusService cvStatusService;

    /**
     * Endpoint pour vérifier le statut d'un CV par email
     * @param email L'email de l'utilisateur
     * @return Le statut du CV
     */
    @GetMapping("/status")
    public ResponseEntity<?> getCvStatus(@RequestParam String email) {
        logger.info("Récupération du statut pour l'email: {}", email);
        Map<String, String> status = cvStatusService.getCvStatus(email);
        return ResponseEntity.ok(status);
    }
    
    /**
     * Endpoint pour marquer un CV comme ouvert par un administrateur
     * @param email L'email du candidat
     * @param adminEmail L'email de l'administrateur
     * @return Un message de confirmation
     */
    @PostMapping("/markAsOpened")
    public ResponseEntity<?> markCvAsOpened(
            @RequestParam String email,
            @RequestParam String adminEmail) {
        
        logger.info("Marquage du CV comme ouvert pour l'email: {} par admin: {}", email, adminEmail);
        boolean success = cvStatusService.markCvAsOpened(email, adminEmail);
        
        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("message", "CV marqué comme ouvert avec succès");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "CV non trouvé pour l'email: " + email);
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Endpoint de test pour vérifier si le contrôleur fonctionne
     * @return Un message de confirmation
     */
    @GetMapping("/status/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Le contrôleur CvStatusController fonctionne correctement!");
    }
}