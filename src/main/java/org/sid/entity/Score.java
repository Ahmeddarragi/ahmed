package org.sid.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "score")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation avec l'entité Offre (Chaque Score est lié à une Offre)
    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = false) // Clé étrangère pour Offre
    private Offre offre;

    // Relation avec l'entité CV (Chaque Score est lié à un CV)
    @ManyToOne
    @JoinColumn(name = "cv_id", nullable = false) // Clé étrangère pour CV
    private CV cv;

    public Score() {
    }

    public Score(Offre offre, CV cv) {
        this.offre = offre;
        this.cv = cv;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Offre getOffre() {
        return offre;
    }

    public void setOffre(Offre offre) {
        this.offre = offre;
    }

    public CV getCv() {
        return cv;
    }

    public void setCv(CV cv) {
        this.cv = cv;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", offre=" + offre +
                ", cv=" + cv +
                '}';
    }
}