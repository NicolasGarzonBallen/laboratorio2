package com.example.subjectservice.DTO;

import com.example.subjectservice.model.Docent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocentResponseDTO {
    private Long id;
    private String name;
    private String certifications;
    private Docent.ContractType typeContract;
    private Double salary;
    private String academy;
    private String email;
}
