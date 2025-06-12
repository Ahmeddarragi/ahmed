package org.sid.controller;

import java.util.List;

import org.sid.entity.Utilisateur;
import org.sid.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/utilisateur")
public class UtilisateurController {
	@Autowired
	private UtilisateurService utilisateurService;
	@PostMapping("/utilisateur")
	public ResponseEntity<Utilisateur> creeUtilisateur(@RequestBody Utilisateur utilisateur){
		return ResponseEntity.ok(utilisateurService.creeUtilisateur(utilisateur));
	}
	@GetMapping("/utilisateur")
	public List<Utilisateur>getAllUtilisateur(){
		return utilisateurService.getAllUtilisateur();	
	}

}