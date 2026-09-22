package com.example.subjectservice.controller;

import com.example.subjectservice.DTO.*;
import com.example.subjectservice.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Tag(name = "Subject", description = "Catalogo de materias: nombre, creditos, programa (id externo)")
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping
    @Operation(summary = "Listar materias con paginacion, ordenamiento y filtros (nombre, programa, semestre, creditos)")
    public ResponseEntity<PaginationResponseDTO<SubjectResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long program,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Integer creditsFrom,
            @RequestParam(required = false) Integer creditsTo) {

        pageSize = Math.min(Math.max(pageSize, 5), 20);
        PaginationDTO pagination = new PaginationDTO(pageNumber, pageSize, sortBy, sortDirection);
        SubjectFilterDTO filter = new SubjectFilterDTO(name, program, semester, creditsFrom, creditsTo);

        Page<SubjectResponseDTO> page = subjectService.getAll(pagination, filter);
        return ResponseEntity.ok(new PaginationResponseDTO<>(page.getContent(), page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages(), page.hasNext(), page.hasPrevious()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener materia por ID")
    public ResponseEntity<SubjectResponseDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(subjectService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear materia")
    public ResponseEntity<?> create(@RequestBody SubjectDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar materia")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        try {
            return ResponseEntity.ok(subjectService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente una materia")
    public ResponseEntity<?> patch(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        try {
            return ResponseEntity.ok(subjectService.patch(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar materia")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            subjectService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
