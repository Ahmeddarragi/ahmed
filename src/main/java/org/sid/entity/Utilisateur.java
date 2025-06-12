package org.sid.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="utilisateurs")
public class Utilisateur {
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long id;
	@Column(name="nom")
	private String nom;
	@Column(name="prenom")
	private String prenom;
	@Column(name="email")
	private String email;
	@Column(name="numero_tel")
	private String numero_tel;
	@Column(name="role")
	private String role;
	

	
	public Utilisateur() {
		
	}
	
	
	public Utilisateur(String role) {
		super();
		this.role = role;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public Utilisateur(String nom, String email, String password, String role) {
		super();
		this.nom = nom;
		this.email = email;
		this.numero_tel = numero_tel;
		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getnumero_tel() {
		return numero_tel;
	}
	public void setPassword(String numero_tel) {
		this.numero_tel = numero_tel;
	}
	
	
	
	
}