package org.sid.controller;

import java.util.List;

import org.sid.entity.Reclamation;
import org.sid.service.ReclamationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reclamations")
@CrossOrigin(origins = "*") // Permettre les requêtes depuis le frontend
public class ReclamationController {
	@Autowired
	private ReclamationService reclamationService;
	
	//ajouter une reclamation 
	@PostMapping("/ajouter")
	public Reclamation ajouterReclamation(@RequestBody Reclamation reclamation) {
		return reclamationService.ajouterReclamation(reclamation);
	}
	//recuperer toutes les reclamations (pour l'admin)
	@GetMapping("/toutes")
	public List<Reclamation> getAllReclamations(){
		return reclamationService.getAllReclamation();
	}
	

}