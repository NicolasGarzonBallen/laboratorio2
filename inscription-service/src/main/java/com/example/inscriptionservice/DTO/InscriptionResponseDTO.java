package com.example.inscriptionservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long cursoId;
    private String courseName;
    private String period;
    private String status;
    private LocalDateTime inscriptionDate;
    private List<GradeResponseDTO> grades;
}
