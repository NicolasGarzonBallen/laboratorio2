package com.example.subjectservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Docente. Entidad raiz propia del modulo Materias (no depende de nadie mas).
 */
@Entity
@Table(name = "docents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Docent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "certifications")
    private String certifications;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_contract", nullable = false)
    private ContractType typeContract;

    @Column(name = "salary")
    private Double salary;

    @Column(name = "academy")
    private String academy;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "docent", cascade = CascadeType.ALL)
    private List<Course> courses = new ArrayList<>();

    public enum ContractType {
        TEMPORAL, OCASIONAL, PLANTA
    }
}
