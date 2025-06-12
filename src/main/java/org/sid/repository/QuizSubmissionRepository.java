package org.sid.repository;

import java.util.List;

import org.sid.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
	List<QuizSubmission> findByEmail(String email);

}