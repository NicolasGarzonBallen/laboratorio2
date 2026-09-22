package com.example.inscriptionservice.service;

import com.example.inscriptionservice.DTO.*;
import com.example.inscriptionservice.client.StudentClient;
import com.example.inscriptionservice.client.SubjectClient;
import com.example.inscriptionservice.management.GradeRepository;
import com.example.inscriptionservice.management.InscriptionRepository;
import com.example.inscriptionservice.management.InscriptionSpecification;
import com.example.inscriptionservice.management.RecordStatusRepository;
import com.example.inscriptionservice.model.Inscription;
import com.example.inscriptionservice.model.RecordStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InscriptionService {
    private final InscriptionRepository inscriptionRepository;
    private final GradeRepository gradeRepository;
    private final RecordStatusRepository recordStatusRepository;
    private final StudentClient studentClient;
    private final SubjectClient subjectClient;

    public Page<InscriptionResponseDTO> getAll(PaginationDTO pagination, InscriptionFilterDTO filter) {
        Sort.Direction direction;
        try {
            direction = Sort.Direction.valueOf(pagination.getSortDirection().toUpperCase());
        } catch (Exception e) {
            direction = Sort.Direction.ASC;
        }
        Pageable pageable = PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(),
            Sort.by(direction, pagination.getSortBy()));

        return inscriptionRepository.findAll(InscriptionSpecification.filter(filter), pageable)
            .map(this::toResponseDTOLight);
    }

    public InscriptionResponseDTO getById(Long id) {
        Inscription inscription = inscriptionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inscripcion no encontrada con id: " + id));
        return toResponseDTOFull(inscription);
    }

    @Transactional
    public InscriptionResponseDTO create(InscriptionDTO dto) {
        // 1. Validar que el estudiante exista (llamada REST a student-service)
        var student = studentClient.getStudentById(dto.getStudentId());
        // 2. Validar que el curso exista y tomar un cupo (llamada REST a subject-service).
        //    enroll() falla con 409 si ya no hay cupos: eso se propaga como error aqui.
        var course = subjectClient.getCourseById(dto.getCursoId());
        subjectClient.enrollInCourse(dto.getCursoId());

        if (inscriptionRepository.findByStudentIdAndCursoIdAndStatus(dto.getStudentId(), dto.getCursoId(), "activa").isPresent()) {
            subjectClient.releaseFromCourse(dto.getCursoId()); // deshacer el cupo tomado
            throw new RuntimeException("El estudiante ya tiene una inscripcion activa en este curso");
        }

        Inscription inscription = new Inscription();
        inscription.setStudentId(dto.getStudentId());
        inscription.setCursoId(dto.getCursoId());
        inscription.setPeriod(dto.getPeriod());
        inscription.setStatus("activa");

        Inscription saved = inscriptionRepository.save(inscription);

        RecordStatus initialStatus = new RecordStatus();
        initialStatus.setPreviousStatus(null);
        initialStatus.setNewStatus("activa");
        initialStatus.setReason("Inscripcion creada");
        initialStatus.setInscription(saved);
        recordStatusRepository.save(initialStatus);

        InscriptionResponseDTO response = toResponseDTOFull(saved);
        response.setStudentName(student.getName() + " " + student.getLastName());
        response.setCourseName(course.getName());
        return response;
    }

    @Transactional
    public InscriptionResponseDTO update(Long id, InscriptionDTO dto) {
        Inscription inscription = inscriptionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inscripcion no encontrada con id: " + id));

        Long studentId = dto.getStudentId() != null ? dto.getStudentId() : inscription.getStudentId();
        Long cursoId = dto.getCursoId() != null ? dto.getCursoId() : inscription.getCursoId();
        String status = dto.getStatus() != null ? dto.getStatus() : inscription.getStatus();
        validateStatus(status);

        var student = studentClient.getStudentById(studentId);
        var course = subjectClient.getCourseById(cursoId);
        boolean courseChanged = !cursoId.equals(inscription.getCursoId());
        boolean wasActive = "activa".equalsIgnoreCase(inscription.getStatus());
        boolean willBeActive = "activa".equalsIgnoreCase(status);

        if (willBeActive) {
            var existing = inscriptionRepository.findByStudentIdAndCursoIdAndStatus(studentId, cursoId, "activa");
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new RuntimeException("El estudiante ya tiene una inscripcion activa en este curso");
            }
        }
        if (wasActive && (!willBeActive || courseChanged)) {
            subjectClient.releaseFromCourse(inscription.getCursoId());
        }
        if (willBeActive && (!wasActive || courseChanged)) {
            subjectClient.enrollInCourse(cursoId);
        }

        String previousStatus = inscription.getStatus();
        inscription.setStudentId(studentId);
        inscription.setCursoId(cursoId);
        inscription.setPeriod(dto.getPeriod() != null ? dto.getPeriod() : inscription.getPeriod());
        inscription.setStatus(status);
        Inscription saved = inscriptionRepository.save(inscription);

        if (!status.equalsIgnoreCase(previousStatus)) {
            RecordStatus history = new RecordStatus();
            history.setPreviousStatus(previousStatus);
            history.setNewStatus(status);
            history.setReason("Inscripcion actualizada");
            history.setInscription(saved);
            recordStatusRepository.save(history);
        }

        InscriptionResponseDTO response = toResponseDTOFull(saved);
        response.setStudentName(student.getName() + " " + student.getLastName());
        response.setCourseName(course.getName());
        return response;
    }

    @Transactional
    public InscriptionResponseDTO changeStatus(Long id, RecordStatusDTO dto) {
        Inscription inscription = inscriptionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inscripcion no encontrada con id: " + id));

        validateStatus(dto.getNewStatus());
        String previous = inscription.getStatus();
        inscription.setStatus(dto.getNewStatus());
        inscriptionRepository.save(inscription);

        RecordStatus history = new RecordStatus();
        history.setPreviousStatus(previous);
        history.setNewStatus(dto.getNewStatus());
        history.setReason(dto.getReason());
        history.setInscription(inscription);
        recordStatusRepository.save(history);

        // Mantiene el cupo sincronizado al cambiar entre estados activos e inactivos.
        if ("cancelada".equalsIgnoreCase(dto.getNewStatus()) && !"cancelada".equalsIgnoreCase(previous)) {
            subjectClient.releaseFromCourse(inscription.getCursoId());
        } else if ("activa".equalsIgnoreCase(dto.getNewStatus()) && !"activa".equalsIgnoreCase(previous)) {
            subjectClient.enrollInCourse(inscription.getCursoId());
        }

        return toResponseDTOFull(inscription);
    }

    @Transactional
    public void delete(Long id) {
        Inscription inscription = inscriptionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inscripcion no encontrada con id: " + id));
        if ("activa".equalsIgnoreCase(inscription.getStatus())) {
            subjectClient.releaseFromCourse(inscription.getCursoId());
        }
        inscriptionRepository.deleteById(id);
    }

    private void validateStatus(String status) {
        if (status == null || !("activa".equalsIgnoreCase(status)
                || "completada".equalsIgnoreCase(status)
                || "cancelada".equalsIgnoreCase(status))) {
            throw new RuntimeException("Estado invalido. Use activa, completada o cancelada");
        }
    }

    // Version liviana para listados (evita 2N llamadas HTTP al paginar)
    private InscriptionResponseDTO toResponseDTOLight(Inscription inscription) {
        InscriptionResponseDTO dto = new InscriptionResponseDTO();
        dto.setId(inscription.getId());
        dto.setStudentId(inscription.getStudentId());
        dto.setCursoId(inscription.getCursoId());
        dto.setPeriod(inscription.getPeriod());
        dto.setStatus(inscription.getStatus());
        dto.setInscriptionDate(inscription.getInscriptionDate());
        dto.setGrades(gradeRepository.findByInscriptionId(inscription.getId()).stream()
            .map(g -> new GradeResponseDTO(g.getId(), g.getType(), g.getValue())).toList());
        return dto;
    }

    // Version completa para lectura individual: si trae nombre de estudiante/curso
    private InscriptionResponseDTO toResponseDTOFull(Inscription inscription) {
        InscriptionResponseDTO dto = toResponseDTOLight(inscription);
        try {
            var student = studentClient.getStudentById(inscription.getStudentId());
            dto.setStudentName(student.getName() + " " + student.getLastName());
        } catch (RuntimeException ignored) { }
        try {
            var course = subjectClient.getCourseById(inscription.getCursoId());
            dto.setCourseName(course.getName());
        } catch (RuntimeException ignored) { }
        return dto;
    }
}
