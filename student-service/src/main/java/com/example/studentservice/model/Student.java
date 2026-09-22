package com.example.studentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "code_number")
    private Integer codeNumber;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Reemplaza al antiguo campo "age" del monolito: ahora se pagina/filtra
    // por año de nacimiento en vez de edad (la edad se puede derivar si hace falta).
    @Column(name = "birth_year", nullable = false)
    private int birthYear;

    @Column(name = "entry_year")
    private int entryYear;

    // N estudiantes : 1 programa. FK real (misma base de datos).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Programa programa;

    // 1 estudiante : 1 contacto. FK real (misma base de datos).
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Contact contact;

    // 1 estudiante : N documentos. FK real (misma base de datos).
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Document> documents = new java.util.ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (this.code != null && !this.code.isEmpty()) {
            try {
                String numericPart = this.code.substring(4);
                this.codeNumber = Integer.parseInt(numericPart);
            } catch (Exception e) {
                this.codeNumber = 0;
            }
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
