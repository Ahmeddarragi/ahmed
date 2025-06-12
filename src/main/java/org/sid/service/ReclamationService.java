package org.sid.service;

import java.util.List;

import org.sid.entity.Reclamation;
import org.sid.repository.ReclamationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReclamationService {
	@Autowired
	private ReclamationRepository reclamationRepository;
	
	public Reclamation ajouterReclamation(Reclamation reclamation) {
		return reclamationRepository.save(reclamation);
	}
	public List<Reclamation> getAllReclamation(){
		return reclamationRepository.findAll();
	}
	
}