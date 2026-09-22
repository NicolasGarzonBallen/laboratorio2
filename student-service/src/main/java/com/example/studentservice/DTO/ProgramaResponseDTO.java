package com.example.studentservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramaResponseDTO {
    private Long idProgram;
    private String name;
    private String faculty;
    private int totalSemesters;
    private long studentCount;
}
