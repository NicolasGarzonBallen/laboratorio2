package com.example.subjectservice.controller;

import com.example.subjectservice.DTO.*;
import com.example.subjectservice.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Course", description = "Oferta real de una materia: horario, periodo, cupo. N:1 Subject, N:1 Docent")
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Listar cursos con paginacion, ordenamiento y filtros (periodo, materia, docente)")
    public ResponseEntity<PaginationResponseDTO<CourseResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long docentId) {

        pageSize = Math.min(Math.max(pageSize, 5), 20);
        PaginationDTO pagination = new PaginationDTO(pageNumber, pageSize, sortBy, sortDirection);
        CourseFilterDTO filter = new CourseFilterDTO(period, subjectId, docentId);

        Page<CourseResponseDTO> page = courseService.getAll(pagination, filter);
        return ResponseEntity.ok(new PaginationResponseDTO<>(page.getContent(), page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages(), page.hasNext(), page.hasPrevious()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener curso por ID (usado tambien por inscription-service y el API Gateway)")
    public ResponseEntity<CourseResponseDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(courseService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear curso (oferta de una materia con un docente, periodo y cupo)")
    public ResponseEntity<?> create(@RequestBody CourseDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar curso")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CourseDTO dto) {
        try {
            return ResponseEntity.ok(courseService.update(id, dto));
        } catch (RuntimeException e) {
            return e.getMessage().contains("no encontrad") ? ResponseEntity.notFound().build()
                : ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar curso")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            courseService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/enroll")
    @Operation(summary = "Tomar un cupo del curso (llamado por inscription-service al crear una inscripcion). " +
                          "Falla si ya no hay cupos disponibles.")
    public ResponseEntity<?> enroll(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(courseService.enroll(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/release")
    @Operation(summary = "Liberar un cupo del curso (llamado por inscription-service al cancelar una inscripcion)")
    public ResponseEntity<?> release(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(courseService.release(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
