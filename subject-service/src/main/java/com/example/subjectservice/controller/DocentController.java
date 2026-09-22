package com.example.subjectservice.controller;

import com.example.subjectservice.DTO.DocentDTO;
import com.example.subjectservice.DTO.DocentResponseDTO;
import com.example.subjectservice.DTO.PaginationResponseDTO;
import com.example.subjectservice.service.DocentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/docents")
@RequiredArgsConstructor
@Tag(name = "Docent", description = "Docentes: entidad raiz propia del modulo Materias")
public class DocentController {
    private final DocentService docentService;

    @GetMapping
    @Operation(summary = "Listar docentes con paginacion y ordenamiento")
    public ResponseEntity<PaginationResponseDTO<DocentResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        pageSize = Math.min(Math.max(pageSize, 5), 20);
        Page<DocentResponseDTO> page = docentService.getAll(pageNumber, pageSize, sortBy, sortDirection);
        return ResponseEntity.ok(new PaginationResponseDTO<>(page.getContent(), page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages(), page.hasNext(), page.hasPrevious()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener docente por ID (usado tambien por el API Gateway en /detalle)")
    public ResponseEntity<DocentResponseDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(docentService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear docente")
    public ResponseEntity<?> create(@RequestBody DocentDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(docentService.create(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar docente")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DocentDTO dto) {
        try {
            return ResponseEntity.ok(docentService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar docente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            docentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
