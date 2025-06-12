package org.sid.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "seen")
    private boolean seen;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructeur par défaut
    public Notification() {
    }

    // ✅ Nouveau constructeur prenant un message
    public Notification(String message) {
        this.message = message;
        this.seen = false; // Par défaut, non lu
        this.createdAt = LocalDateTime.now();
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSeen() {
        return seen;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}