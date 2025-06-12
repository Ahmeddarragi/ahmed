package org.sid.service;

import java.util.List;

import org.sid.entity.Offre;
import org.sid.repository.OffreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OffreService {
	@Autowired
	private OffreRepository offreRepository;
	
	public Offre creerOffre(Offre offre) {
		return offreRepository.save(offre);
	}
	public List<Offre>getAllOffre(){
		return offreRepository.findAll();
	}
	public Offre getOffreById(Long id) {
		return offreRepository.findById(id).orElseThrow(()->new RuntimeException("offre non trouvée"));
	}
	public void supprimeOffre(Long id) {
		offreRepository.deleteById(id);
	}
	public Offre updateOffre(Long id, Offre nouvelleOffre) {
		Offre offreExistante=offreRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Offre non trouvée avec l'id : " + id));
		 offreExistante.setTitre(nouvelleOffre.getTitre());
		    offreExistante.setDescription(nouvelleOffre.getDescription());
		    offreExistante.setLocalisation(nouvelleOffre.getLocalisation());
		    offreExistante.setCompetences(nouvelleOffre.getCompetences());
		    return offreRepository.save(offreExistante);
		
		
	}

}