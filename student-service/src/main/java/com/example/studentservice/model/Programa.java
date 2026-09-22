package com.example.studentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Carrera/programa academico. Relacion real dentro del modulo Estudiantes:
 * N estudiantes pertenecen a 1 programa (N:1). Es una FK real porque Student
 * y Programa viven en la MISMA base de datos (students_db).
 *
 * OJO: el modulo Materias tambien tiene un campo "program" en Subject, pero
 * ahi NO es una FK -- es solo un numero (el id de este programa) porque
 * Materias vive en otra base de datos y no puede tener una FK cruzando modulos.
 */
@Entity
@Table(name = "programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Programa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_program", nullable = false)
    private Long idProgram;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "total_semesters")
    private int totalSemesters;

    @OneToMany(mappedBy = "programa", cascade = CascadeType.ALL)
    private List<Student> students = new ArrayList<>();
}
