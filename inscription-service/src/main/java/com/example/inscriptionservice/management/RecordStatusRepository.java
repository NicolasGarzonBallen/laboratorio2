package com.example.inscriptionservice.management;

import com.example.inscriptionservice.model.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecordStatusRepository extends JpaRepository<RecordStatus, Long> {
    List<RecordStatus> findByInscriptionIdOrderByChangeDateAsc(Long inscriptionId);
}
