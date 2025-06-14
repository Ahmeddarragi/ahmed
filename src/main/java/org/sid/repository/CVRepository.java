package org.sid.repository;

import java.util.List;

import org.sid.entity.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface CVRepository extends JpaRepository<CV, Long> {
	List<CV> findByCompetencesContainingIgnoreCase(String skill);
	CV findByEmail(String email);
}