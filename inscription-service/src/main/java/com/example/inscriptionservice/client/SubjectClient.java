package com.example.inscriptionservice.client;

import com.example.inscriptionservice.DTO.CourseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente hacia subject-service. Ademas de leer un curso, avisa cuando se
 * toma o libera un cupo (para que enrolledCount se actualice en tiempo real
 * en la base de datos DUEÑA de esa informacion, no en la de Inscripciones).
 */
@Component
public class SubjectClient {
    private final RestTemplate restTemplate;
    private final String subjectServiceUrl;

    public SubjectClient(RestTemplate restTemplate, @Value("${services.subject.url}") String subjectServiceUrl) {
        this.restTemplate = restTemplate;
        this.subjectServiceUrl = subjectServiceUrl;
    }

    public CourseDTO getCourseById(Long id) {
        try {
            return restTemplate.getForObject(subjectServiceUrl + "/api/courses/" + id, CourseDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Curso no encontrado con id: " + id);
        } catch (RestClientException e) {
            throw new RuntimeException("No se pudo contactar a subject-service: " + e.getMessage());
        }
    }

    public void enrollInCourse(Long courseId) {
        try {
            restTemplate.postForObject(subjectServiceUrl + "/api/courses/" + courseId + "/enroll", null, CourseDTO.class);
        } catch (HttpClientErrorException.Conflict e) {
            throw new RuntimeException("El curso ya no tiene cupos disponibles");
        } catch (RestClientException e) {
            throw new RuntimeException("No se pudo contactar a subject-service: " + e.getMessage());
        }
    }

    public void releaseFromCourse(Long courseId) {
        try {
            restTemplate.postForObject(subjectServiceUrl + "/api/courses/" + courseId + "/release", null, CourseDTO.class);
        } catch (RestClientException e) {
            // Si subject-service no responde al liberar, no bloqueamos la cancelacion
            // de la inscripcion; queda como inconsistencia a reconciliar despues.
        }
    }
}
