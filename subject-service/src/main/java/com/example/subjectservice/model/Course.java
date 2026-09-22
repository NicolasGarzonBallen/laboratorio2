package com.example.subjectservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Oferta real de una materia en un periodo especifico: N cursos : 1 Subject,
 * N cursos : 1 Docent. Ambas son FK reales porque las tres entidades viven en
 * la MISMA base de datos (subjects_db).
 *
 * "capacity" y "enrolledCount" se actualizan en tiempo real cuando
 * inscription-service llama a /api/courses/{id}/enroll o /release al crear o
 * cancelar una inscripcion. enrolledCount nunca puede superar capacity.
 */
@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "period", nullable = false)
    private String period; // ej. "2026-2"

    @Column(name = "schedule")
    private String schedule; // horario, ej. "Lunes-Miercoles 8:00-10:00"

    @Column(name = "capacity", nullable = false)
    private int capacity; // cupo total

    @Column(name = "enrolled_count", nullable = false)
    private int enrolledCount = 0; // cupos ya tomados

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docent_id", nullable = false)
    private Docent docent;
}
