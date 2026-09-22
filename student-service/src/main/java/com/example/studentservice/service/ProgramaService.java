package com.example.studentservice.service;

import com.example.studentservice.DTO.ProgramaDTO;
import com.example.studentservice.DTO.ProgramaResponseDTO;
import com.example.studentservice.management.ProgramaRepository;
import com.example.studentservice.model.Programa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramaService {
    private final ProgramaRepository programaRepository;

    public List<ProgramaResponseDTO> getAllProgramas() {
        return programaRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    public ProgramaResponseDTO getProgramaById(Long id) {
        Programa programa = programaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Programa no encontrado con id: " + id));
        return toResponseDTO(programa);
    }

    @Transactional
    public ProgramaResponseDTO createPrograma(ProgramaDTO dto) {
        if (programaRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Ya existe un programa con nombre " + dto.getName());
        }
        Programa programa = new Programa();
        programa.setName(dto.getName());
        programa.setFaculty(dto.getFaculty());
        programa.setTotalSemesters(dto.getTotalSemesters());
        return toResponseDTO(programaRepository.save(programa));
    }

    @Transactional
    public ProgramaResponseDTO updatePrograma(Long id, ProgramaDTO dto) {
        Programa programa = programaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Programa no encontrado con id: " + id));
        programa.setName(dto.getName());
        programa.setFaculty(dto.getFaculty());
        programa.setTotalSemesters(dto.getTotalSemesters());
        return toResponseDTO(programaRepository.save(programa));
    }

    @Transactional
    public void deletePrograma(Long id) {
        if (!programaRepository.existsById(id)) {
            throw new RuntimeException("Programa no encontrado con id: " + id);
        }
        programaRepository.deleteById(id);
    }

    @Transactional
    public void initializePrograms() {
        if (programaRepository.count() == 0) {
            String[][] programs = {
                {"Administracion de Empresas", "Escuela de Administracion e Ingenieria Industrial", "10"},
                {"Contaduria Publica", "Escuela de Ciencias Administrativas y Economicas", "10"},
                {"Finanzas y Comercio Internacional", "Escuela de Ciencias Administrativas y Economicas", "10"},
                {"Ingenieria de Minas", "Escuela de Ingenieria de Minas", "10"},
                {"Ingenieria Geologica", "Escuela de Ingenieria Geologica", "10"},
                {"Ingenieria Industrial", "Escuela de Administracion e Ingenieria Industrial", "10"},
                {"Ingenieria Electronica", "Escuela de Ingenieria Electronica", "10"},
                {"Ingenieria de Sistemas y Computacion", "Escuela de Ingenieria de Sistemas y Computacion", "10"}
            };
            for (String[] p : programs) {
                Programa programa = new Programa();
                programa.setName(p[0]);
                programa.setFaculty(p[1]);
                programa.setTotalSemesters(Integer.parseInt(p[2]));
                programaRepository.save(programa);
            }
        }
    }

    private ProgramaResponseDTO toResponseDTO(Programa programa) {
        return new ProgramaResponseDTO(
            programa.getIdProgram(),
            programa.getName(),
            programa.getFaculty(),
            programa.getTotalSemesters(),
            programa.getStudents() != null ? programa.getStudents().size() : 0
        );
    }
}
