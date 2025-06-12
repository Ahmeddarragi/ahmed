package org.sid.repository;
import org.sid.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ScoreRepository extends JpaRepository<Score,Long>{

}