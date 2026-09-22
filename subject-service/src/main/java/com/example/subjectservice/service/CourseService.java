package com.example.subjectservice.service;

import com.example.subjectservice.DTO.*;
import com.example.subjectservice.management.CourseRepository;
import com.example.subjectservice.management.DocentRepository;
import com.example.subjectservice.management.SubjectRepository;
import com.example.subjectservice.model.Course;
import com.example.subjectservice.model.Docent;
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
public class CourseService {
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final DocentRepository docentRepository;

    public Page<CourseResponseDTO> getAll(PaginationDTO pagination, CourseFilterDTO filter) {
        Sort.Direction direction;
        try {
            direction = Sort.Direction.valueOf(pagination.getSortDirection().toUpperCase());
        } catch (Exception e) {
            direction = Sort.Direction.ASC;
        }
        Pageable pageable = PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(),
            Sort.by(direction, pagination.getSortBy()));

        if (filter.getSubjectId() != null) {
            return wrap(courseRepository.findBySubjectId(filter.getSubjectId()), pageable);
        }
        if (filter.getDocentId() != null) {
            return wrap(courseRepository.findByDocentId(filter.getDocentId()), pageable);
        }
        if (filter.getPeriod() != null) {
            return wrap(courseRepository.findByPeriod(filter.getPeriod()), pageable);
        }
        return courseRepository.findAll(pageable).map(this::toResponseDTO);
    }

    private Page<CourseResponseDTO> wrap(java.util.List<Course> list, Pageable pageable) {
        var dtoList = list.stream().map(this::toResponseDTO).toList();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtoList.size());
        var pageContent = start >= dtoList.size() ? java.util.List.<CourseResponseDTO>of() : dtoList.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, dtoList.size());
    }

    public CourseResponseDTO getById(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado con id: " + id));
        return toResponseDTO(course);
    }

    @Transactional
    public CourseResponseDTO create(CourseDTO dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId())
            .orElseThrow(() -> new RuntimeException("Materia no encontrada con id: " + dto.getSubjectId()));
        Docent docent = docentRepository.findById(dto.getDocentId())
            .orElseThrow(() -> new RuntimeException("Docente no encontrado con id: " + dto.getDocentId()));

        Course course = new Course();
        course.setName(dto.getName());
        course.setPeriod(dto.getPeriod());
        course.setSchedule(dto.getSchedule());
        course.setCapacity(dto.getCapacity());
        course.setEnrolledCount(0);
        course.setSubject(subject);
        course.setDocent(docent);
        return toResponseDTO(courseRepository.save(course));
    }

    @Transactional
    public CourseResponseDTO update(Long id, CourseDTO dto) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado con id: " + id));

        if (dto.getCapacity() < course.getEnrolledCount()) {
            throw new RuntimeException("La nueva capacidad no puede ser menor a los cupos ya tomados (" +
                course.getEnrolledCount() + ")");
        }

        Subject subject = subjectRepository.findById(dto.getSubjectId())
            .orElseThrow(() -> new RuntimeException("Materia no encontrada con id: " + dto.getSubjectId()));
        Docent docent = docentRepository.findById(dto.getDocentId())
            .orElseThrow(() -> new RuntimeException("Docente no encontrado con id: " + dto.getDocentId()));

        course.setName(dto.getName());
        course.setPeriod(dto.getPeriod());
        course.setSchedule(dto.getSchedule());
        course.setCapacity(dto.getCapacity());
        course.setSubject(subject);
        course.setDocent(docent);
        return toResponseDTO(courseRepository.save(course));
    }

    @Transactional
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Curso no encontrado con id: " + id);
        }
        courseRepository.deleteById(id);
    }

    /**
     * Llamado por inscription-service (via HTTP) al crear una inscripcion.
     * "synchronized" evita que dos inscripciones concurrentes lean el mismo
     * enrolledCount y ambas crean que queda cupo cuando solo queda uno.
     * Para produccion real esto se resolveria mejor con un lock optimista
     * (@Version) o SELECT ... FOR UPDATE, pero para el laboratorio esto
     * ya demuestra el problema de consistencia y su solucion basica.
     */
    @Transactional
    public synchronized CourseResponseDTO enroll(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado con id: " + courseId));
        if (course.getEnrolledCount() >= course.getCapacity()) {
            throw new RuntimeException("El curso " + course.getName() + " ya no tiene cupos disponibles");
        }
        course.setEnrolledCount(course.getEnrolledCount() + 1);
        return toResponseDTO(courseRepository.save(course));
    }

    /** Llamado por inscription-service al cancelar/eliminar una inscripcion. */
    @Transactional
    public synchronized CourseResponseDTO release(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado con id: " + courseId));
        if (course.getEnrolledCount() > 0) {
            course.setEnrolledCount(course.getEnrolledCount() - 1);
        }
        return toResponseDTO(courseRepository.save(course));
    }

    private CourseResponseDTO toResponseDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setPeriod(course.getPeriod());
        dto.setSchedule(course.getSchedule());
        dto.setCapacity(course.getCapacity());
        dto.setEnrolledCount(course.getEnrolledCount());
        dto.setAvailableSlots(course.getCapacity() - course.getEnrolledCount());
        dto.setSubjectId(course.getSubject().getId());
        dto.setSubjectName(course.getSubject().getName());
        dto.setDocentId(course.getDocent().getId());
        dto.setDocentName(course.getDocent().getName());
        return dto;
    }
}
