package com.example.studentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI studentServiceOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("Student Service API")
            .description("Modulo Estudiantes: Student (raiz), Programa (N:1), Contact (1:1), Document (1:N). " +
                         "Laboratorio 3 - Arquitectura Modular con API Gateway.")
            .version("v1.0"));
    }
}
