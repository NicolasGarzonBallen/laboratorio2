package com.example.inscriptionservice.management;

import com.example.inscriptionservice.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByInscriptionId(Long inscriptionId);
}
