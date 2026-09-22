package com.example.inscriptionservice.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Contrato minimo de lectura hacia student-service (no es la entidad JPA). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentDTO {
    private Long id;
    private String code;
    private String name;
    private String lastName;
}
