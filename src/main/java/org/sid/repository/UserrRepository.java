package org.sid.repository;

import org.sid.entity.Userr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserrRepository extends JpaRepository<Userr, Long> {
	Userr findByUsername(String username);
	Userr findByEmail(String email);

	
}