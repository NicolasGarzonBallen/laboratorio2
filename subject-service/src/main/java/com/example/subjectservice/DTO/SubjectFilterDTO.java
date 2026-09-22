package com.example.subjectservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectFilterDTO {
    private String name;
    private Long program;
    private Integer semester;
    private Integer creditsFrom;
    private Integer creditsTo;
}
