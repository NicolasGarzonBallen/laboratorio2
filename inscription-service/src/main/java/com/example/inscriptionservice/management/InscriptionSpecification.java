package com.example.inscriptionservice.management;

import com.example.inscriptionservice.DTO.InscriptionFilterDTO;
import com.example.inscriptionservice.model.Inscription;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class InscriptionSpecification {
    public static Specification<Inscription> filter(InscriptionFilterDTO filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.getStudentId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("studentId"), filter.getStudentId()));
            }
            if (filter.getCursoId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("cursoId"), filter.getCursoId()));
            }
            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), filter.getStatus()));
            }
            if (filter.getPeriod() != null && !filter.getPeriod().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("period"), filter.getPeriod()));
            }

            return predicate;
        };
    }
}
