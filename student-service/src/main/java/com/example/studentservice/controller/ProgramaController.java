package com.example.studentservice.controller;

import com.example.studentservice.DTO.ProgramaDTO;
import com.example.studentservice.DTO.ProgramaResponseDTO;
import com.example.studentservice.service.ProgramaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
@Tag(name = "Programa", description = "Carreras/programas academicos. N estudiantes : 1 programa")
public class ProgramaController {
    private final ProgramaService programaService;

    @GetMapping
    @Operation(summary = "Listar todos los programas")
    public ResponseEntity<List<ProgramaResponseDTO>> getAll() {
        return ResponseEntity.ok(programaService.getAllProgramas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener programa por ID")
    public ResponseEntity<ProgramaResponseDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(programaService.getProgramaById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear programa")
    public ResponseEntity<?> create(@RequestBody ProgramaDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(programaService.createPrograma(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar programa")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProgramaDTO dto) {
        try {
            return ResponseEntity.ok(programaService.updatePrograma(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar programa")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            programaService.deletePrograma(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
