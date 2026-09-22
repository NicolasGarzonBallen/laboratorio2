package com.example.studentservice.controller;

import com.example.studentservice.service.ProgramaService;
import com.example.studentservice.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/initialize")
@RequiredArgsConstructor
@Tag(name = "Initialize", description = "Carga de datos de prueba")
public class InitializeController {
    private final ProgramaService programaService;
    private final StudentService studentService;

    @PostMapping("/programs")
    @Operation(summary = "Inicializar los 8 programas academicos")
    public ResponseEntity<String> initPrograms() {
        try {
            programaService.initializePrograms();
            return ResponseEntity.ok("8 programas inicializados exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(409).body("Programas ya inicializados o error: " + e.getMessage());
        }
    }

    @PostMapping("/students")
    @Operation(summary = "Inicializar 1000 estudiantes (requiere programas ya creados)")
    public ResponseEntity<String> initStudents() {
        try {
            studentService.initializeStudents();
            return ResponseEntity.ok("1000 estudiantes inicializados exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(409).body("Estudiantes ya inicializados o error: " + e.getMessage());
        }
    }
}
