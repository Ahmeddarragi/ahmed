package org.sid.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="reponse_condidat")
public class ReponseCandidat {
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	 @ManyToOne
	 @JoinColumn(name = "condidat", nullable = false)
	private Userr user;
	@Column(name="id-question")
	 private Long questionId;
	@Column(name="reponse")
	private String reponse;
	@Column(name="score")
	private int score;
	
	public ReponseCandidat() {
		
	}
	public ReponseCandidat(Userr user, Long questionId, String reponse, int score) {
		super();
		this.user = user;
		this.questionId = questionId;
		this.reponse = reponse;
		this.score = score;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Userr getUser() {
		return user;
	}
	public void setUser(Userr user) {
		this.user = user;
	}
	public Long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	public String getReponse() {
		return reponse;
	}
	public void setReponse(String reponse) {
		this.reponse = reponse;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
}