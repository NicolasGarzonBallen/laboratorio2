package com.example.studentservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private String code;
    private String name;
    private String lastName;
    private String email;
    private int birthYear;
    private int entryYear;
    private Long programId;
}
