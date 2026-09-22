package com.example.inscriptionservice.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Contrato minimo de lectura hacia subject-service (no es la entidad JPA). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDTO {
    private Long id;
    private String name;
    private int capacity;
    private int enrolledCount;
    private int availableSlots;
}
