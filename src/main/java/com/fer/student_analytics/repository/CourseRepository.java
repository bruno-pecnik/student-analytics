package com.fer.student_analytics.repository;

import com.fer.student_analytics.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByAcademicYearId(UUID academicYearId);
    boolean existsByCodeAndAcademicYearId(String code, UUID academicYearId); // provjera duplikata po code-u unutar iste akademske godine
    boolean existsByNameAndAcademicYearId(String name, UUID academicYearId); // provjera duplikata po nazivu unutar iste akademske godine
}