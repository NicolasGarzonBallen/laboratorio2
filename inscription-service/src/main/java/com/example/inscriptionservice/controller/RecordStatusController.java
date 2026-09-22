package com.example.inscriptionservice.controller;

import com.example.inscriptionservice.DTO.RecordStatusResponseDTO;
import com.example.inscriptionservice.service.RecordStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/inscriptions/{inscriptionId}/status-history")
@RequiredArgsConstructor
@Tag(name = "RecordStatus", description = "Historial de estados de una inscripcion. Relacion 1:N")
public class RecordStatusController {
    private final RecordStatusService recordStatusService;

    @GetMapping
    @Operation(summary = "Ver historial de cambios de estado de una inscripcion")
    public ResponseEntity<List<RecordStatusResponseDTO>> getHistory(@PathVariable Long inscriptionId) {
        return ResponseEntity.ok(recordStatusService.getHistory(inscriptionId));
    }
}
