package com.example.studentservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterDTO {
    private String name;
    private String lastName;
    private String code;
    private Integer birthYear;
    private Integer birthYearFrom;
    private Integer birthYearTo;
    private Long programId;
}
