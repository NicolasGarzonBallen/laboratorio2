package com.example.subjectservice.management;

import com.example.subjectservice.DTO.CourseFilterDTO;
import com.example.subjectservice.model.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class CourseSpecification {
    private CourseSpecification() {
    }

    public static Specification<Course> filter(CourseFilterDTO filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (filter.getPeriod() != null && !filter.getPeriod().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("period"), filter.getPeriod()));
            }
            if (filter.getSubjectId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("subject").get("id"), filter.getSubjectId()));
            }
            if (filter.getDocentId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("docent").get("id"), filter.getDocentId()));
            }
            return predicate;
        };
    }
}
