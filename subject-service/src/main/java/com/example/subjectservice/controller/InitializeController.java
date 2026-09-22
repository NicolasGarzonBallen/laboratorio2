package com.example.subjectservice.controller;

import com.example.subjectservice.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/initialize")
@RequiredArgsConstructor
@Tag(name = "Initialize", description = "Carga de datos de prueba")
public class InitializeController {
    private final SubjectService subjectService;

    @PostMapping("/subjects")
    @Operation(summary = "Inicializar las 50 materias de Ingenieria de Sistemas y Computacion",
        description = "Recibe el id del programa 'Ingenieria de Sistemas y Computacion' tal como quedo " +
                       "registrado en student-service (GET /api/programs en ese modulo).")
    public ResponseEntity<String> initSubjects(@Parameter(description = "id del programa Sistemas en student-service")
                                                @RequestParam Long systemsProgramId) {
        try {
            subjectService.initializeSystemsSubjects(systemsProgramId);
            return ResponseEntity.ok("50 materias de Sistemas inicializadas exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(409).body("Materias ya inicializadas o error: " + e.getMessage());
        }
    }
}
