package com.example.subjectservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseFilterDTO {
    private String period;
    private Long subjectId;
    private Long docentId;
}
