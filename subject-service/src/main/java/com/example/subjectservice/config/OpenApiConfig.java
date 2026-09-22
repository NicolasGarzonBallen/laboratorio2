package com.example.subjectservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI subjectServiceOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("Subject Service API")
            .description("Modulo Materias: Subject (catalogo), Docent (raiz propia), Course (oferta real: N:1 Subject, N:1 Docent). " +
                         "Laboratorio 3 - Arquitectura Modular con API Gateway.")
            .version("v1.0"));
    }
}
