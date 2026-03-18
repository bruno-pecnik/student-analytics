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

    // in java we have a whole object but hibernate only stores it's ID
    @ManyToOne // multiple courses belong to one academic year
    @JoinColumn(name = "academic_year_id", nullable = false) // JoinColumn tells me in Course table there is column called academic_year_id
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