package org.sid.repository;

import org.sid.entity.CV;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CvStatusRepository extends JpaRepository<CV, Long> {
    Optional<CV> findByEmail(String email);
}