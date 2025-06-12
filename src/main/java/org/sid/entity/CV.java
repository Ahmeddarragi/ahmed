package org.sid.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cv")
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "email")
    private String email;

    @Column(name = "numero_tel")
    private String numero_tel;

    @Column(name = "role")
    private String role;

    @Column(name = "fichier", nullable = false)
    private String fichier;

    @Column(name = "experience", nullable = false)
    private Double experience;

    @Column(name = "competences", columnDefinition = "TEXT", nullable = false)
    private String competences;

    @Column(name = "lien_linkedin")
    private String lienLinkedin;

    @Column(name = "lien_github")
    private String lienGithub;
    
    // Ajoutez ces colonnes avec nullable=true pour éviter les erreurs sur les données existantes
    @Column(name = "status", nullable = true)
    private String status;
    
    @Column(name = "opened_by", nullable = true)
    private String openedBy;
    
    @Column(name = "opened_at", nullable = true)
    private java.util.Date openedAt;

    // Constructeurs
    public CV() {
        // Initialiser le statut par défaut pour les nouveaux CV
        this.status = "PENDING";
    }

    public CV(String nom, String prenom, String email, String numero_tel, String role,
              String fichier, Double experience, String competences,
              String lienLinkedin, String lienGithub) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numero_tel = numero_tel;
        this.role = role;
        this.fichier = fichier;
        this.experience = experience;
        this.competences = competences;
        this.lienLinkedin = lienLinkedin;
        this.lienGithub = lienGithub;
        this.status = "PENDING";
    }

    // Getters & Setters existants...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNumero_tel() { return numero_tel; }
    public void setNumero_tel(String numero_tel) { this.numero_tel = numero_tel; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFichier() { return fichier; }
    public void setFichier(String fichier) { this.fichier = fichier; }

    public Double getExperience() { return experience; }
    public void setExperience(Double experience) { this.experience = experience; }

    public String getCompetences() { return competences; }
    public void setCompetences(String competences) { this.competences = competences; }

    public String getLienLinkedin() { return lienLinkedin; }
    public void setLienLinkedin(String lienLinkedin) { this.lienLinkedin = lienLinkedin; }

    public String getLienGithub() { return lienGithub; }
    public void setLienGithub(String lienGithub) { this.lienGithub = lienGithub; }
    
    // Nouveaux getters et setters
    public String getStatus() {
        return status != null ? status : "PENDING"; // Valeur par défaut si null
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getOpenedBy() {
        return openedBy;
    }
    
    public void setOpenedBy(String openedBy) {
        this.openedBy = openedBy;
    }
    
    public java.util.Date getOpenedAt() {
        return openedAt;
    }
    
    public void setOpenedAt(java.util.Date openedAt) {
        this.openedAt = openedAt;
    }
}