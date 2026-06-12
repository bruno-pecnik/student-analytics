package com.fer.student_analytics.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // u javi imamo cijeli objekt ali hibernate sačuva samo njegov ID
    @ManyToOne // više kolegija pripadaju jednoj akademskoj godini
    @JoinColumn(name = "academic_year_id", nullable = false) // JoinColumn mi kaže u Course tablici da ima stupac academic_year_id
    private AcademicYear academicYear;

    @Column(nullable = false) // nullable=false vrijedi samo za prvu liniju ispod
    private String name;

    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    public enum Semester {
        WINTER, SUMMER
    }
}