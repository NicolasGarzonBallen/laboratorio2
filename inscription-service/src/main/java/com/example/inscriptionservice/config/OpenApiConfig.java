package com.example.inscriptionservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI inscriptionServiceOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("Inscription Service API")
            .description("Modulo Inscripciones: Inscription (raiz, estudianteId/cursoId como ids externos sin FK), " +
                         "Grade (1:N), RecordStatus (1:N). Laboratorio 3 - Arquitectura Modular con API Gateway.")
            .version("v1.0"));
    }
}
