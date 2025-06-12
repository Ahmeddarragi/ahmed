package org.sid.service;

import java.util.List;

import org.sid.entity.Offre;
import org.sid.entity.Utilisateur;
import org.sid.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {
	@Autowired
	private UtilisateurRepository utilisateurRepository;
	
	public Utilisateur creeUtilisateur(Utilisateur utilisateur) {
		return utilisateurRepository.save(utilisateur);
	}
	public List<Utilisateur>getAllUtilisateur(){
		return utilisateurRepository.findAll();
	}

}