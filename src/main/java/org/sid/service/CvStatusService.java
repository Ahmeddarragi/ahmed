package org.sid.service;

import org.sid.entity.CV;
import org.sid.repository.CVRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CvStatusService {
    
    private static final Logger logger = LoggerFactory.getLogger(CvStatusService.class);

    @Autowired
    private CVRepository cvRepository;
    
    /**
     * Récupère le statut du CV pour un email donné
     * @param email L'email de l'utilisateur
     * @return Une map contenant le statut du CV
     */
    public Map<String, String> getCvStatus(String email) {
        Map<String, String> response = new HashMap<>();
        
        try {
            CV cv = cvRepository.findByEmail(email);
            
            if (cv != null) {
                // Retourner le statut spécifique du CV
                String status = cv.getStatus();
                response.put("status", status);
                
                // Si le CV a été ouvert, ajouter des informations supplémentaires
                if ("OPENED".equals(status) && cv.getOpenedAt() != null) {
                    response.put("message", "CV ouvert par le recruteur et en cours d'examination");
                    response.put("openedAt", cv.getOpenedAt().toString());
                } else if ("PROCESSING".equals(status)) {
                    response.put("message", "CV en cours de traitement");
                } else {
                    response.put("message", "CV en attente de traitement");
                }
            } else {
                // Si aucun CV n'existe, le statut est en attente
                response.put("status", "PENDING");
                response.put("message", "Vous pouvez vous reconnecter pour voir l'état de votre CV");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du statut du CV: {}", e.getMessage());
            response.put("status", "ERROR");
            response.put("message", "Une erreur est survenue lors de la récupération du statut");
        }
        
        return response;
    }
    
    /**
     * Marque un CV comme ouvert par un administrateur
     * @param candidateEmail L'email du candidat
     * @param adminEmail L'email de l'administrateur
     * @return true si le CV a été marqué comme ouvert, false sinon
     */
    public boolean markCvAsOpened(String candidateEmail, String adminEmail) {
        try {
            CV cv = cvRepository.findByEmail(candidateEmail);
            
            if (cv != null) {
                cv.setStatus("OPENED");
                cv.setOpenedBy(adminEmail);
                cv.setOpenedAt(new Date());
                cvRepository.save(cv);
                logger.info("CV marqué comme ouvert pour l'email: {}", candidateEmail);
                return true;
            } else {
                logger.warn("CV non trouvé pour l'email: {}", candidateEmail);
            }
        } catch (Exception e) {
            logger.error("Erreur lors du marquage du CV comme ouvert: {}", e.getMessage());
        }
        
        return false;
    }
}