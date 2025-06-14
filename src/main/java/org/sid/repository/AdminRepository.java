package org.sid.repository;

import java.util.Optional;

import org.sid.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	Optional<Admin>findByUsername(String username);
	Optional<Admin>findByEmail(String email);
	

}