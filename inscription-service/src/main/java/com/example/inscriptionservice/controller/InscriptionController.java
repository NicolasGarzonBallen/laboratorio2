package com.example.inscriptionservice.controller;

import com.example.inscriptionservice.DTO.*;
import com.example.inscriptionservice.service.InscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
@Tag(name = "Inscription", description = "Entidad raiz. studentId/cursoId son ids externos, sin FK real")
public class InscriptionController {
    private final InscriptionService inscriptionService;

    @GetMapping
    @Operation(summary = "Listar inscripciones con paginacion, ordenamiento y filtros (estudiante, curso, estado, periodo)")
    public ResponseEntity<PaginationResponseDTO<InscriptionResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long cursoId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String period) {

        pageSize = Math.min(Math.max(pageSize, 5), 20);
        PaginationDTO pagination = new PaginationDTO(pageNumber, pageSize, sortBy, sortDirection);
        InscriptionFilterDTO filter = new InscriptionFilterDTO(studentId, cursoId, status, period);

        Page<InscriptionResponseDTO> page = inscriptionService.getAll(pagination, filter);
        return ResponseEntity.ok(new PaginationResponseDTO<>(page.getContent(), page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages(), page.hasNext(), page.hasPrevious()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inscripcion por ID, con notas incluidas (usado tambien por el API Gateway en /detalle)")
    public ResponseEntity<InscriptionResponseDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(inscriptionService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear inscripcion",
        description = "Valida el estudiante en student-service y toma un cupo en subject-service (POST /api/courses/{id}/enroll). " +
                      "Si el curso esta lleno, falla con 409.")
    public ResponseEntity<?> create(@RequestBody InscriptionDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(inscriptionService.create(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reemplazar una inscripcion")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InscriptionDTO dto) {
        try {
            return ResponseEntity.ok(inscriptionService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente una inscripcion")
    public ResponseEntity<?> patch(@PathVariable Long id, @RequestBody InscriptionDTO dto) {
        try {
            return ResponseEntity.ok(inscriptionService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar el estado de una inscripcion (activa/completada/cancelada). " +
                          "Registra el cambio en el historial y libera el cupo si se cancela.")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestBody RecordStatusDTO dto) {
        try {
            return ResponseEntity.ok(inscriptionService.changeStatus(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar inscripcion (libera el cupo del curso en subject-service)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            inscriptionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
