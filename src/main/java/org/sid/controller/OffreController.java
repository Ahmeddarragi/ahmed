package org.sid.controller;

import java.util.List;

import org.sid.entity.Offre;
import org.sid.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/offres")
public class OffreController {
	@Autowired
	private OffreService offreService;
	@PostMapping("/offre")
	public ResponseEntity<Offre> creerOffre(@RequestBody Offre offre) {
        return ResponseEntity.ok(offreService.creerOffre(offre));
    }
	 @GetMapping("/offre")
	    public List<Offre> getAllOffres() {
	        return offreService.getAllOffre();
	}
	  @GetMapping("offre/{id}")
	    public ResponseEntity<Offre> getOffreById(@PathVariable Long id) {
	        return ResponseEntity.ok(offreService.getOffreById(id));
	    }
	  
	  @DeleteMapping("offre/{id}")
	    public ResponseEntity<Void> supprimerOffre(@PathVariable Long id) {
	        offreService.supprimeOffre(id);
	        return ResponseEntity.noContent().build();
	    }
	  @PutMapping("/offre/{id}")
	  public ResponseEntity<Offre> updateOffre(@PathVariable Long id, @RequestBody Offre nouvelleOffre) {
	      Offre updated = offreService.updateOffre(id, nouvelleOffre);
	      return ResponseEntity.ok(updated);
	  }
	 
	

}