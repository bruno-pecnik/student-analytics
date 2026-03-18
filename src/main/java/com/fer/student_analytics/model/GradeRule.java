package com.fer.student_analytics.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "grade_rule")
public class GradeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private Integer grade;

    @Column(name = "min_points")
    private Float minPoints;

    @Column(name = "max_points")
    private Float maxPoints;
}