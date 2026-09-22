package com.example.inscriptionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad raiz del modulo. estudianteId y cursoId son ids externos (student-service
 * y subject-service respectivamente) -- nunca una FK real, porque viven en otra
 * base de datos. Grade y RecordStatus si son relaciones reales (1:N), porque
 * viven en esta misma base de datos (inscriptions_db).
 */
@Entity
@Table(name = "inscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "curso_id", nullable = false)
    private Long cursoId;

    @Column(name = "period")
    private String period;

    @Column(name = "status", nullable = false)
    private String status; // activa, completada, cancelada

    @Column(name = "inscription_date")
    private LocalDateTime inscriptionDate;

    @OneToMany(mappedBy = "inscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();

    @OneToMany(mappedBy = "inscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordStatus> statusHistory = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (inscriptionDate == null) inscriptionDate = LocalDateTime.now();
        if (status == null) status = "activa";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
