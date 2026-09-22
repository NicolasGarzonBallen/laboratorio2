package com.example.subjectservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponseDTO {
    private Long id;
    private String code;
    private String name;
    private int credits;
    private int semester;
    private Long program;
    private long courseCount;
}
