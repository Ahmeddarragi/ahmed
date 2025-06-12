package org.sid.repository;

import org.sid.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {

}