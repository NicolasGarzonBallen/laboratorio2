package com.example.studentservice.controller;

import com.example.studentservice.DTO.DocumentDTO;
import com.example.studentservice.DTO.DocumentResponseDTO;
import com.example.studentservice.service.DocumentService;
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
@Tag(name = "Document", description = "Documentos de un estudiante. Relacion 1:N")
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping("/students/{studentId}/documents")
    @Operation(summary = "Listar documentos de un estudiante")
    public ResponseEntity<List<DocumentResponseDTO>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(documentService.getDocumentsByStudentId(studentId));
    }

    @PostMapping("/students/{studentId}/documents")
    @Operation(summary = "Agregar un documento a un estudiante")
    public ResponseEntity<?> add(@PathVariable Long studentId, @RequestBody DocumentDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(documentService.addDocument(studentId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/documents/{documentId}")
    @Operation(summary = "Actualizar un documento")
    public ResponseEntity<?> update(@PathVariable Long documentId, @RequestBody DocumentDTO dto) {
        try {
            return ResponseEntity.ok(documentService.updateDocument(documentId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/documents/{documentId}")
    @Operation(summary = "Eliminar un documento")
    public ResponseEntity<Void> delete(@PathVariable Long documentId) {
        try {
            documentService.deleteDocument(documentId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
