package com.example.inscriptionservice.service;

import com.example.inscriptionservice.DTO.RecordStatusResponseDTO;
import com.example.inscriptionservice.management.RecordStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordStatusService {
    private final RecordStatusRepository recordStatusRepository;

    public List<RecordStatusResponseDTO> getHistory(Long inscriptionId) {
        return recordStatusRepository.findByInscriptionIdOrderByChangeDateAsc(inscriptionId).stream()
            .map(r -> new RecordStatusResponseDTO(r.getId(), r.getPreviousStatus(), r.getNewStatus(),
                r.getReason(), r.getChangeDate()))
            .toList();
    }
}
