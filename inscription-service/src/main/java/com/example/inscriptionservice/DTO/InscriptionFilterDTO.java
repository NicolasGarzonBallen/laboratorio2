package com.example.inscriptionservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionFilterDTO {
    private Long studentId;
    private Long cursoId;
    private String status;
    private String period;
}
