package org.sid.repository;

import org.sid.entity.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

}