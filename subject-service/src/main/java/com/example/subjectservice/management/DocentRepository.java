package com.example.subjectservice.management;

import com.example.subjectservice.model.Docent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocentRepository extends JpaRepository<Docent, Long> {
}
