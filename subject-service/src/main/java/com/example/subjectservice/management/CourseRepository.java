package com.example.subjectservice.management;

import com.example.subjectservice.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    List<Course> findBySubjectId(Long subjectId);
    List<Course> findByDocentId(Long docentId);
    List<Course> findByPeriod(String period);
}
