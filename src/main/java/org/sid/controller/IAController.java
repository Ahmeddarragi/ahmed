package org.sid.controller;

import org.sid.entity.CV;
import org.sid.repository.CVRepository;
import org.sid.service.IAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/cv")
public class IAController {

    private static final Logger logger = LoggerFactory.getLogger(IAController.class);

    @Autowired
    private IAService iaService;

    @Autowired
    private CVRepository cvRepository;

    // ✅ Ajouté : Endpoint pour voir le CV
    @GetMapping("/view/{fileName:.+}")
    public ResponseEntity<Resource> afficherCV(@PathVariable String fileName) {
        try {
            String dossierStockage = System.getProperty("user.home") + "/cv_storage";
            Path filePath = Paths.get(dossierStockage).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (MalformedURLException e) {
            logger.error("Erreur lors de l'accès au fichier : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCV(@RequestParam("nom") String nom,
                                      @RequestParam("prenom") String prenom,
                                      @RequestParam("email") String email,
                                      @RequestParam("numero_tel") String numeroTel,
                                      @RequestParam("role") String role,
                                      @RequestParam("fichier") MultipartFile fichier,
                                      @RequestParam(value = "linkedin", required = false) String linkedin,
                                      @RequestParam(value = "github", required = false) String github,
                                      @RequestParam(value = "chemin", required = false) String cheminDynamique) {
        try {
            if (fichier.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erreur", "Fichier non fourni ou vide"));
            }

            if (!fichier.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                return ResponseEntity.badRequest().body(Map.of("erreur", "Seuls les fichiers PDF sont autorisés"));
            }

            String dossierStockage = (cheminDynamique != null && !cheminDynamique.isEmpty())
                    ? cheminDynamique
                    : System.getProperty("user.home") + "/cv_storage";

            File dossier = new File(dossierStockage);
            if (!dossier.exists()) dossier.mkdirs();

            String fichierUnique = UUID.randomUUID() + "_" + fichier.getOriginalFilename();
            String cheminFichier = dossierStockage + "/" + fichierUnique;
            fichier.transferTo(new File(cheminFichier));

            Map<String, String> infos = iaService.extraireInformations(cheminFichier);

            String nomExtrait = nom;
            String competences = infos.getOrDefault("competences", "Aucune compétence identifiée");

            String experienceStr = infos.getOrDefault("experiences", "0");
            Double experienceAnnee = 0.0;
            try {
                experienceAnnee = Double.parseDouble(experienceStr.replace(" ans", "").trim());
                experienceAnnee = Math.round(experienceAnnee * 10.0) / 10.0;
            } catch (NumberFormatException e) {
                logger.error("Erreur de parsing de l'expérience : {}", e.getMessage());
            }

            CV cv = iaService.enregistrerCV(nomExtrait, prenom, email, numeroTel, role, cheminFichier, experienceAnnee, competences, linkedin, github);
            
            // Définir le statut initial
            cv.setStatus("PROCESSING");
            cvRepository.save(cv);

            return ResponseEntity.ok(Map.of(
                    "message", "CV téléchargé avec succès",
                    "cvId", cv.getId().toString(),
                    "cheminStockage", fichierUnique  // Important : renvoyer juste le nom du fichier
            ));
        } catch (Exception e) {
            logger.error("Erreur lors de l'upload : {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("erreur", "Erreur lors de l'upload : " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCVs() {
        try {
            List<CV> cvList = cvRepository.findAll();
            if (cvList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Collections.singletonMap("message", "Aucun CV trouvé."));
            }
            
            // Convertir les CV en Map pour inclure le statut
            List<Map<String, Object>> result = new ArrayList<>();
            for (CV cv : cvList) {
                Map<String, Object> cvMap = new HashMap<>();
                cvMap.put("id", cv.getId());
                cvMap.put("nom", cv.getNom());
                cvMap.put("prenom", cv.getPrenom());
                cvMap.put("email", cv.getEmail());
                cvMap.put("numero_tel", cv.getNumero_tel());
                cvMap.put("role", cv.getRole());
                cvMap.put("fichier", cv.getFichier());
                cvMap.put("experience", cv.getExperience());
                cvMap.put("competences", cv.getCompetences());
                cvMap.put("lienLinkedin", cv.getLienLinkedin());
                cvMap.put("lienGithub", cv.getLienGithub());
                cvMap.put("status", cv.getStatus() != null ? cv.getStatus() : "PENDING");
                
                result.add(cvMap);
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des CVs : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("erreur", "Erreur lors de la récupération des CVs"));
        }
    }

    @GetMapping("/scores")
    public ResponseEntity<?> getAllScores() {
        try {
            List<CV> cvList = cvRepository.findAll();
            if (cvList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Collections.singletonMap("message", "Aucun CV trouvé."));
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (CV cv : cvList) {
                Map<String, Object> map = new HashMap<>();
                map.put("email", cv.getEmail());
                map.put("experience", cv.getExperience() != null ? cv.getExperience() : 0);
                map.put("status", cv.getStatus() != null ? cv.getStatus() : "PENDING");
                result.add(map);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des scores : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("erreur", "Erreur lors de la récupération des scores"));
        }
    }
  
    @DeleteMapping("condidat/{id}")
    public ResponseEntity<Void> supprimeCondidat(@PathVariable Long id) {
        iaService.supprimeCondidat(id);
        return ResponseEntity.noContent().build();
    }
}