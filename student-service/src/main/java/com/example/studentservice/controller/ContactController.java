package com.example.studentservice.controller;

import com.example.studentservice.DTO.ContactDTO;
import com.example.studentservice.DTO.ContactResponseDTO;
import com.example.studentservice.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students/{studentId}/contact")
@RequiredArgsConstructor
@Tag(name = "Contact", description = "Contacto de un estudiante. Relacion 1:1")
public class ContactController {
    private final ContactService contactService;

    @GetMapping
    @Operation(summary = "Obtener el contacto de un estudiante")
    public ResponseEntity<ContactResponseDTO> getByStudent(@PathVariable Long studentId) {
        try {
            return ResponseEntity.ok(contactService.getContactByStudentId(studentId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear el contacto de un estudiante (solo uno permitido, 1:1)")
    public ResponseEntity<?> create(@PathVariable Long studentId, @RequestBody ContactDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(contactService.createContact(studentId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    @Operation(summary = "Actualizar el contacto de un estudiante")
    public ResponseEntity<?> update(@PathVariable Long studentId, @RequestBody ContactDTO dto) {
        try {
            return ResponseEntity.ok(contactService.updateContact(studentId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    @Operation(summary = "Eliminar el contacto de un estudiante")
    public ResponseEntity<Void> delete(@PathVariable Long studentId) {
        try {
            contactService.deleteContact(studentId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
