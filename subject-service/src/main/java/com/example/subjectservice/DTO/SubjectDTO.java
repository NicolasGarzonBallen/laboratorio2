package com.example.subjectservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private String code;
    private String name;
    private int credits;
    private int semester;
    private Long program; // id externo del programa (student-service), sin FK real
}
