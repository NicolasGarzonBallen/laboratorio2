package com.example.inscriptionservice.service;

import com.example.inscriptionservice.DTO.GradeDTO;
import com.example.inscriptionservice.DTO.GradeResponseDTO;
import com.example.inscriptionservice.management.GradeRepository;
import com.example.inscriptionservice.management.InscriptionRepository;
import com.example.inscriptionservice.model.Grade;
import com.example.inscriptionservice.model.Inscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
    private final InscriptionRepository inscriptionRepository;

    public List<GradeResponseDTO> getByInscription(Long inscriptionId) {
        return gradeRepository.findByInscriptionId(inscriptionId).stream()
            .map(g -> new GradeResponseDTO(g.getId(), g.getType(), g.getValue())).toList();
    }

    @Transactional
    public GradeResponseDTO addGrade(Long inscriptionId, GradeDTO dto) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
            .orElseThrow(() -> new RuntimeException("Inscripcion no encontrada con id: " + inscriptionId));
        Grade grade = new Grade();
        grade.setType(dto.getType());
        grade.setValue(dto.getValue());
        grade.setInscription(inscription);
        Grade saved = gradeRepository.save(grade);
        return new GradeResponseDTO(saved.getId(), saved.getType(), saved.getValue());
    }

    @Transactional
    public void deleteGrade(Long gradeId) {
        if (!gradeRepository.existsById(gradeId)) {
            throw new RuntimeException("Nota no encontrada con id: " + gradeId);
        }
        gradeRepository.deleteById(gradeId);
    }
}
