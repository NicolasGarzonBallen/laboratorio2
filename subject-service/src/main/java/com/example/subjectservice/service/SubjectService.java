package com.example.subjectservice.service;

import com.example.subjectservice.DTO.*;
import com.example.subjectservice.management.SubjectRepository;
import com.example.subjectservice.management.SubjectSpecification;
import com.example.subjectservice.model.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public Page<SubjectResponseDTO> getAll(PaginationDTO pagination, SubjectFilterDTO filter) {
        Sort.Direction direction;
        try {
            direction = Sort.Direction.valueOf(pagination.getSortDirection().toUpperCase());
        } catch (Exception e) {
            direction = Sort.Direction.ASC;
        }
        Pageable pageable = PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(),
            Sort.by(direction, pagination.getSortBy()));

        if (filter != null && hasFilter(filter)) {
            return subjectRepository.findAll(SubjectSpecification.filter(filter), pageable).map(this::toResponseDTO);
        }
        return subjectRepository.findAll(pageable).map(this::toResponseDTO);
    }

    private boolean hasFilter(SubjectFilterDTO f) {
        return f.getName() != null || f.getProgram() != null || f.getSemester() != null ||
               f.getCreditsFrom() != null || f.getCreditsTo() != null;
    }

    public SubjectResponseDTO getById(Long id) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Materia no encontrada con id: " + id));
        return toResponseDTO(subject);
    }

    public boolean existsById(Long id) {
        return subjectRepository.existsById(id);
    }

    @Transactional
    public SubjectResponseDTO create(SubjectDTO dto) {
        if (dto.getCode() != null && subjectRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Ya existe una materia con codigo " + dto.getCode());
        }
        Subject subject = new Subject();
        subject.setCode(dto.getCode());
        subject.setName(dto.getName());
        subject.setCredits(dto.getCredits());
        subject.setSemester(dto.getSemester());
        subject.setProgram(dto.getProgram());
        return toResponseDTO(subjectRepository.save(subject));
    }

    @Transactional
    public SubjectResponseDTO update(Long id, SubjectDTO dto) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Materia no encontrada con id: " + id));
        subject.setName(dto.getName());
        subject.setCredits(dto.getCredits());
        subject.setSemester(dto.getSemester());
        subject.setProgram(dto.getProgram());
        return toResponseDTO(subjectRepository.save(subject));
    }

    @Transactional
    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Materia no encontrada con id: " + id);
        }
        subjectRepository.deleteById(id);
    }

    @Transactional
    public void initializeSystemsSubjects(Long systemsProgramId) {
        if (subjectRepository.count() == 0) {
            Object[][] subjects = {
                {"1", "Algoritmos y Programacion", 4, 1}, {"2", "Programacion I", 4, 1},
                {"3", "Programacion II", 4, 2}, {"4", "Programacion III", 4, 3},
                {"5", "Calculo I", 4, 1}, {"6", "Calculo II", 4, 2},
                {"7", "Calculo III", 4, 3}, {"8", "Calculo IV", 4, 4},
                {"9", "Catedra Universidad y Entorno", 2, 1}, {"10", "Fisica I", 4, 2},
                {"11", "Fisica II", 4, 3}, {"12", "Fisica III", 4, 4},
                {"13", "Competencias Comunicativas", 3, 1}, {"14", "Algebra Lineal", 3, 2},
                {"15", "Socio Humanistica I", 3, 2}, {"16", "Etica y Politica", 3, 3},
                {"17", "Economia", 3, 3}, {"18", "Fisica II", 4, 3},
                {"19", "Expresion Grafica y Geometria Descriptiva", 3, 2}, {"20", "Socio Humanistica II", 3, 4},
                {"21", "Probabilidad y Estadistica", 4, 4}, {"22", "Metodologia de la Investigacion", 3, 5},
                {"23", "Bases de Datos I", 4, 5}, {"24", "Metodos Numericos", 3, 5},
                {"25", "Electronica General", 4, 5}, {"26", "Teoria General de Sistemas", 3, 6},
                {"27", "Ingenieria de Requisitos", 3, 6}, {"28", "Bases de Datos II", 4, 6},
                {"29", "Matematicas Discretas", 3, 6}, {"30", "Comunicaciones", 3, 7},
                {"31", "Investigacion de Operaciones", 4, 7}, {"32", "Ingenieria del Software I", 3, 7},
                {"33", "Sistemas Distribuidos", 4, 8}, {"34", "Lenguajes Formales", 3, 8},
                {"35", "Transmision de Datos", 3, 8}, {"36", "Sistemas Operativos", 4, 8},
                {"37", "Ingenieria del Software II", 3, 9}, {"38", "Inteligencia Computacional", 4, 9},
                {"39", "Redes de Datos", 4, 9}, {"40", "Arquitectura de Computadores", 4, 9},
                {"41", "Administracion", 3, 10}, {"42", "Trabajo de Campo", 2, 10},
                {"43", "Simulacion por Computador", 4, 10}, {"44", "Seminario de Trabajo de Grado", 2, 10},
                {"45", "Auditoria de Sistemas", 3, 10}, {"46", "Gerencia Informatica", 3, 10},
                {"47", "Electiva I", 3, 9}, {"48", "Electiva II", 3, 10},
                {"49", "Electiva III", 3, 10}, {"50", "Electiva IV", 3, 10}
            };
            for (Object[] s : subjects) {
                Subject subject = new Subject();
                subject.setCode("SIS-" + s[0]);
                subject.setName((String) s[1]);
                subject.setCredits((int) s[2]);
                subject.setSemester((int) s[3]);
                subject.setProgram(systemsProgramId);
                subjectRepository.save(subject);
            }
        }
    }

    private SubjectResponseDTO toResponseDTO(Subject subject) {
        return new SubjectResponseDTO(subject.getId(), subject.getCode(), subject.getName(),
            subject.getCredits(), subject.getSemester(), subject.getProgram(),
            subject.getCourses() != null ? subject.getCourses().size() : 0);
    }
}
