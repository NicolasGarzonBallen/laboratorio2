package com.example.inscriptionservice.management;

import com.example.inscriptionservice.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long>, JpaSpecificationExecutor<Inscription> {
    List<Inscription> findByStudentId(Long studentId);
    List<Inscription> findByCursoId(Long cursoId);
    Optional<Inscription> findByStudentIdAndCursoIdAndStatus(Long studentId, Long cursoId, String status);
}
