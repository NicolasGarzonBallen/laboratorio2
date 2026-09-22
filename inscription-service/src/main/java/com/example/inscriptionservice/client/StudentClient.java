package com.example.inscriptionservice.client;

import com.example.inscriptionservice.DTO.StudentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class StudentClient {
    private final RestTemplate restTemplate;
    private final String studentServiceUrl;

    public StudentClient(RestTemplate restTemplate, @Value("${services.student.url}") String studentServiceUrl) {
        this.restTemplate = restTemplate;
        this.studentServiceUrl = studentServiceUrl;
    }

    public StudentDTO getStudentById(Long id) {
        try {
            return restTemplate.getForObject(studentServiceUrl + "/api/students/" + id, StudentDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Estudiante no encontrado con id: " + id);
        } catch (RestClientException e) {
            throw new RuntimeException("No se pudo contactar a student-service: " + e.getMessage());
        }
    }
}
