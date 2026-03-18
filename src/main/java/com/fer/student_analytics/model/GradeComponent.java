package com.fer.student_analytics.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "grade_component")
public class GradeComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne // one course has multiple components, but one component has only one course
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String name;

    @Column(name = "weight_percent")
    private Float weightPercent;

    @Column(name = "max_points")
    private Float maxPoints;

    @Column(name = "passing_threshold")
    private Float passingThreshold;

    @Column(name = "is_required")
    private Boolean isRequired;
}