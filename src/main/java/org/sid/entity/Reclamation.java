package org.sid.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "reclamations")
public class Reclamation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    // Constructeurs
    public Reclamation() {
    }

    public Reclamation(String email, String message) {
        this.email = email;
        this.message = message;
    }

    // Initialisation automatique de la date d'envoi avant l'insertion
    @PrePersist
    protected void onCreate() {
        dateEnvoi = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    // Méthode toString()
    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", message='" + message + '\'' +
                ", dateEnvoi=" + dateEnvoi +
                '}';
    }
}