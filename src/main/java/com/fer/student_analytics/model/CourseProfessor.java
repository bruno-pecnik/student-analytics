package com.fer.student_analytics.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "course_professor")
public class CourseProfessor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne // this means we have same course_ID in multiple rows in the table
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne // 
    @JoinColumn(name = "professor_id", nullable = false)
    private SystemUser professor;
}