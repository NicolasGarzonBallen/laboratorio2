package com.example.studentservice.management;

import com.example.studentservice.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    Optional<Student> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByEmail(String email);
    List<Student> findByProgramaIdProgram(Long programId);
}
