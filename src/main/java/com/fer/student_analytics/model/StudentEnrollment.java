package com.fer.student_analytics.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "student_enrollment")
public class StudentEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private SystemUser student;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private CourseGroup group;
}