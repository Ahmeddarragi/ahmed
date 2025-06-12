package org.sid.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.sid.entity.CV;
import org.sid.repository.CVRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IAService {

    private static final Logger logger = LoggerFactory.getLogger(IAService.class);

    @Autowired
    private CVRepository cvRepository;

    public CV enregistrerCV(String nom, String prenom, String email, String numeroTel, String role,
                             String fichier, Double experience, String competences,
                             String linkedin, String github) {
        CV cv = new CV();
        cv.setNom(nom);
        cv.setPrenom(prenom);
        cv.setEmail(email);
        cv.setNumero_tel(numeroTel);
        cv.setRole(role);
        cv.setFichier(fichier);
        cv.setExperience(experience);
        cv.setCompetences(competences);
        cv.setLienLinkedin(linkedin);
        cv.setLienGithub(github);
        return cvRepository.save(cv);
    }

    public Map<String, String> extraireInformations(String fichier) {
        Map<String, String> infos = new HashMap<>();
        try (PDDocument document = PDDocument.load(new File(fichier))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String contenu = stripper.getText(document).replaceAll("\\s+", " ").trim();

            // Extraction du nom
            String nom = extraireNom(contenu);
            infos.put("nom", nom);

            // Extraction des compétences
            String competences = extraireCompetences(contenu);
            infos.put("competences", competences);

            // Extraction de l'expérience
            double anneesExperience = calculerExperienceEnAnnées(contenu);
            String anneesStr = String.format(Locale.US, "%.1f", anneesExperience);
            infos.put("experiences", anneesStr + " ans");

        } catch (IOException e) {
            logger.error("Erreur lors de la lecture du fichier PDF : {}", e.getMessage());
            infos.put("erreur", "Erreur lors de la lecture du fichier PDF");
        }
        return infos;
    }

    private String extraireNom(String contenu) {
        Pattern pattern = Pattern.compile("(?i)(nom|name)[\\s:-]+([A-Za-zÀ-ÖØ-öø-ÿ\\s-]+)(?=\\R|$)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(contenu);
        return matcher.find() ? matcher.group(2).trim() : "Nom inconnu";
    }

    private String extraireCompetences(String contenu) {
        List<String> competencesConnues = Arrays.asList(
            "Java", "Spring Boot", "Spring", "Angular", "React", "Node.js", "Python",
            "HTML", "CSS", "JavaScript", "TypeScript", "Docker", "Kubernetes", "SQL",
            "MySQL", "MongoDB", "AWS"
        );

        Set<String> competencesTrouvees = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String competence : competencesConnues) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(competence) + "\\b", Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(contenu).find()) {
                competencesTrouvees.add(competence);
            }
        }

        return competencesTrouvees.isEmpty()
                ? "Aucune compétence identifiée"
                : String.join(", ", competencesTrouvees);
    }

    private double calculerExperienceEnAnnées(String contenu) {
        logger.info("Calcul de l'expérience pour le contenu: {}", contenu.substring(0, Math.min(100, contenu.length())) + "...");
        
        // Extraction des périodes de dates au format dd/MM/yyyy
        List<Periode> periodes = new ArrayList<>();
        Pattern patternDates = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})\\s*[-–—]\\s*(\\d{2}/\\d{2}/\\d{4})", Pattern.CASE_INSENSITIVE);
        Matcher matcherDates = patternDates.matcher(contenu);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate now = LocalDate.now();
        
        while (matcherDates.find()) {
            try {
                String debutStr = matcherDates.group(1);
                String finStr = matcherDates.group(2);
                
                LocalDate debut = LocalDate.parse(debutStr, formatter);
                LocalDate fin = LocalDate.parse(finStr, formatter);
                
                if (!fin.isBefore(debut)) {
                    periodes.add(new Periode(debut, fin));
                    logger.debug("Période trouvée: {} à {}", debutStr, finStr);
                }
            } catch (DateTimeParseException e) {
                logger.error("Erreur de parsing de date: {}", e.getMessage());
            }
        }
        
        // Calculer la durée totale des périodes (en tenant compte des chevauchements)
        double totalMois = calculerDureeTotale(periodes);
        double anneesParDates = totalMois / 12.0;
        
        logger.info("Expérience calculée par dates: {} années ({} mois)", 
                   String.format("%.1f", anneesParDates), totalMois);
        
        // Recherche d'une mention directe d'expérience (ex: "5 ans d'expérience")
        Pattern patternAnnees = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s+(?:an(?:s|nées?)?|year(?:s)?)", Pattern.CASE_INSENSITIVE);
        Matcher matcherAnnees = patternAnnees.matcher(contenu);
        
        double anneesDirectes = 0.0;
        while (matcherAnnees.find()) {
            try {
                String valeurStr = matcherAnnees.group(1).replace(',', '.');
                double valeur = Double.parseDouble(valeurStr);
                
                // Ne prendre en compte que si c'est une valeur raisonnable (entre 0 et 50 ans)
                if (valeur > 0 && valeur <= 50) {
                    anneesDirectes = Math.max(anneesDirectes, valeur);
                    logger.debug("Mention directe d'expérience trouvée: {} ans", valeur);
                }
            } catch (NumberFormatException e) {
                logger.error("Erreur de parsing de l'expérience: {}", e.getMessage());
            }
        }
        
        logger.info("Expérience mentionnée directement: {} années", String.format("%.1f", anneesDirectes));
        
        // Prendre la valeur la plus fiable
        double resultat;
        if (anneesParDates > 0 && (anneesDirectes == 0 || Math.abs(anneesParDates - anneesDirectes) < 2)) {
            resultat = anneesParDates; // Utiliser le calcul par dates s'il est disponible et cohérent
        } else if (anneesDirectes > 0) {
            resultat = anneesDirectes; // Sinon utiliser la mention directe si disponible
        } else {
            resultat = anneesParDates; // Par défaut, utiliser le calcul par dates
        }
        
        // Arrondir à une décimale
        resultat = Math.round(resultat * 10.0) / 10.0;
        logger.info("Expérience totale calculée: {} années", resultat);
        
        return resultat;
    }

    // Classe pour représenter une période de travail
    private static class Periode {
        LocalDate debut;
        LocalDate fin;
        
        Periode(LocalDate debut, LocalDate fin) {
            this.debut = debut;
            this.fin = fin;
        }
    }

    // Méthode pour calculer la durée totale des périodes en tenant compte des chevauchements
    private double calculerDureeTotale(List<Periode> periodes) {
        if (periodes.isEmpty()) {
            return 0;
        }
        
        // Trier les périodes par date de début
        periodes.sort(Comparator.comparing(p -> p.debut));
        
        // Fusionner les périodes qui se chevauchent
        List<Periode> periodesUnifiees = new ArrayList<>();
        Periode periodeActuelle = periodes.get(0);
        
        for (int i = 1; i < periodes.size(); i++) {
            Periode suivante = periodes.get(i);
            
            // Si la période suivante commence avant ou le même jour que la fin de la période actuelle
            if (!suivante.debut.isAfter(periodeActuelle.fin)) {
                // Étendre la période actuelle si nécessaire
                if (suivante.fin.isAfter(periodeActuelle.fin)) {
                    periodeActuelle = new Periode(periodeActuelle.debut, suivante.fin);
                }
            } else {
                // Ajouter la période actuelle et passer à la suivante
                periodesUnifiees.add(periodeActuelle);
                periodeActuelle = suivante;
            }
        }
        
        // Ajouter la dernière période
        periodesUnifiees.add(periodeActuelle);
        
        // Calculer la durée totale en mois
        double totalMois = 0;
        for (Periode p : periodesUnifiees) {
            Period period = Period.between(p.debut, p.fin);
            int mois = period.getYears() * 12 + period.getMonths();
            // Ajouter un mois supplémentaire si plus de 15 jours
            if (period.getDays() > 15) {
                mois += 1;
            }
            totalMois += mois;
            
            logger.debug("Période unifiée: {} à {}, durée: {} mois", 
                        p.debut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        p.fin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                        mois);
        }
        
        return totalMois;
    }
	public void supprimeCondidat(Long id) {
		cvRepository.deleteById(id);
	}
}