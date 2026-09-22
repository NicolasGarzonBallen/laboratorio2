package com.example.inscriptionservice.controller;

import com.example.inscriptionservice.DTO.GradeDTO;
import com.example.inscriptionservice.DTO.GradeResponseDTO;
import com.example.inscriptionservice.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Grade", description = "Notas de una inscripcion. Relacion 1:N")
public class GradeController {
    private final GradeService gradeService;

    @GetMapping("/inscriptions/{inscriptionId}/grades")
    @Operation(summary = "Listar notas de una inscripcion")
    public ResponseEntity<List<GradeResponseDTO>> getByInscription(@PathVariable Long inscriptionId) {
        return ResponseEntity.ok(gradeService.getByInscription(inscriptionId));
    }

    @PostMapping("/inscriptions/{inscriptionId}/grades")
    @Operation(summary = "Agregar una nota a una inscripcion")
    public ResponseEntity<?> add(@PathVariable Long inscriptionId, @RequestBody GradeDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(gradeService.addGrade(inscriptionId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/grades/{gradeId}")
    @Operation(summary = "Eliminar una nota")
    public ResponseEntity<Void> delete(@PathVariable Long gradeId) {
        try {
            gradeService.deleteGrade(gradeId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
