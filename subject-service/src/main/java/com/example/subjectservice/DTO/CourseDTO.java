package com.example.subjectservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private String name;
    private String period;
    private String schedule;
    private int capacity;
    private Long subjectId;
    private Long docentId;
}
