package com.example.inscriptionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/** 1 inscripcion : N cambios de estado (historial). FK real, misma base de datos. */
@Entity
@Table(name = "record_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "previous_status")
    private String previousStatus;

    @Column(name = "new_status", nullable = false)
    private String newStatus;

    @Column(name = "reason")
    private String reason;

    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscription_id", nullable = false)
    private Inscription inscription;

    @PrePersist
    protected void onCreate() {
        if (changeDate == null) changeDate = LocalDateTime.now();
    }
}
