package com.example.subjectservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo de materias. Entidad raiz de este modulo.
 * "program" NO es una FK: es solo el id numerico del programa que vive en
 * student-service (otra base de datos). Ver README para la regla de
 * independencia: ningun modulo puede tener una FK real hacia otro modulo.
 */
@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "credits", nullable = false)
    private int credits;

    @Column(name = "semester")
    private int semester;

    // Referencia externa al id de Programa en student-service. Solo un numero.
    @Column(name = "program", nullable = false)
    private Long program;

    @Column(name = "code", unique = true)
    private String code;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Course> courses = new ArrayList<>();
}
