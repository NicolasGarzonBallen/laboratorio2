package com.example.subjectservice.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @JsonIgnoreProperties(ignoreUnknown = true): este mismo DTO lo consume
 * inscription-service y el API Gateway al llamar a este modulo por HTTP;
 * si este servicio agrega campos nuevos, esos clientes no se rompen.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseResponseDTO {
    private Long id;
    private String name;
    private String period;
    private String schedule;
    private int capacity;
    private int enrolledCount;
    private int availableSlots;
    private Long subjectId;
    private String subjectName;
    private Long docentId;
    private String docentName;
}
