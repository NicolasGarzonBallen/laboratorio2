package com.example.studentservice.controller;

import com.example.studentservice.DTO.*;
import com.example.studentservice.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Entidad raiz del modulo Estudiantes")
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Listar estudiantes con paginacion, ordenamiento y filtros (nombre, codigo, año de nacimiento, programa)")
    public ResponseEntity<PaginationResponseDTO<StudentResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Integer birthYear,
            @RequestParam(required = false) Integer birthYearFrom,
            @RequestParam(required = false) Integer birthYearTo,
            @RequestParam(required = false) Long programId) {

        pageSize = Math.min(Math.max(pageSize, 5), 20);
        PaginationDTO pagination = new PaginationDTO(pageNumber, pageSize, sortBy, sortDirection);
        FilterDTO filter = new FilterDTO(name, lastName, code, birthYear, birthYearFrom, birthYearTo, programId);

        Page<StudentResponseDTO> page = studentService.getAllStudents(pagination, filter);
        return ResponseEntity.ok(new PaginationResponseDTO<>(page.getContent(), page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages(), page.hasNext(), page.hasPrevious()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener estudiante por ID (usado tambien por inscription-service y el API Gateway)")
    public ResponseEntity<StudentResponseDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(studentService.getStudentById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear estudiante")
    public ResponseEntity<?> create(@RequestBody StudentDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estudiante completo")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody StudentDTO dto) {
        try {
            return ResponseEntity.ok(studentService.updateStudent(id, dto));
        } catch (RuntimeException e) {
            return e.getMessage().contains("no encontrado") ? ResponseEntity.notFound().build()
                : ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar estudiante parcialmente")
    public ResponseEntity<?> patch(@PathVariable Long id, @RequestBody StudentDTO dto) {
        try {
            return ResponseEntity.ok(studentService.patchStudent(id, dto));
        } catch (RuntimeException e) {
            return e.getMessage().contains("no encontrado") ? ResponseEntity.notFound().build()
                : ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar estudiante")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
