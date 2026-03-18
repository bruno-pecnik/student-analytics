package com.fer.student_analytics.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "student_record")
public class StudentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne // multiple records for one enrollment. the same student has multiple records
    @JoinColumn(name = "enrollment_id", nullable = false)
    private StudentEnrollment enrollment;

    @ManyToOne  //*** multiple records for one component. multiple students have the same test
    @JoinColumn(name = "component_id", nullable = false)
    private GradeComponent component;

    private Float points;

    @Column(name = "obligation_met")
    private Boolean obligationMet;

    private String note;

    @Column(name = "recorded_at")
    private LocalDate recordedAt;
}