package com.example.subjectservice.management;

import com.example.subjectservice.DTO.SubjectFilterDTO;
import com.example.subjectservice.model.Subject;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class SubjectSpecification {
    public static Specification<Subject> filter(SubjectFilterDTO filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%"));
            }
            if (filter.getProgram() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("program"), filter.getProgram()));
            }
            if (filter.getSemester() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("semester"), filter.getSemester()));
            }
            if (filter.getCreditsFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("credits"), filter.getCreditsFrom()));
            }
            if (filter.getCreditsTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("credits"), filter.getCreditsTo()));
            }

            return predicate;
        };
    }
}
