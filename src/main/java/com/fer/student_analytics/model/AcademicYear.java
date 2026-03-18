package com.fer.student_analytics.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "academic_year")
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

}