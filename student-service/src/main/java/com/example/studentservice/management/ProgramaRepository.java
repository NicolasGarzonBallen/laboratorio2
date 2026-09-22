package com.example.studentservice.management;

import com.example.studentservice.model.Programa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProgramaRepository extends JpaRepository<Programa, Long> {
    Optional<Programa> findByName(String name);
}
