package com.example.studentservice.management;

import com.example.studentservice.model.Student;
import com.example.studentservice.DTO.FilterDTO;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class StudentSpecification {
    public static Specification<Student> filterStudents(FilterDTO filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%"));
            }
            if (filter.getLastName() != null && !filter.getLastName().isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("lastName")),
                    "%" + filter.getLastName().toLowerCase() + "%"));
            }
            if (filter.getCode() != null && !filter.getCode().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("code"), "%" + filter.getCode() + "%"));
            }
            // Filtro por año de nacimiento (reemplaza al filtro por edad del monolito)
            if (filter.getBirthYear() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("birthYear"), filter.getBirthYear()));
            }
            if (filter.getBirthYearFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("birthYear"), filter.getBirthYearFrom()));
            }
            if (filter.getBirthYearTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("birthYear"), filter.getBirthYearTo()));
            }
            // Filtro por programa (tercer filtro relevante exigido por el PDF)
            if (filter.getProgramId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("programa").get("idProgram"), filter.getProgramId()));
            }

            return predicate;
        };
    }
}
