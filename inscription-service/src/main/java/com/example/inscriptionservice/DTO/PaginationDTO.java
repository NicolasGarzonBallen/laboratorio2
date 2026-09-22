package com.example.inscriptionservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDTO {
    private int pageNumber;
    private int pageSize;
    private String sortBy;
    private String sortDirection;
}
