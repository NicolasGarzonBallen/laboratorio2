package com.example.inscriptionservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordStatusResponseDTO {
    private Long id;
    private String previousStatus;
    private String newStatus;
    private String reason;
    private LocalDateTime changeDate;
}
