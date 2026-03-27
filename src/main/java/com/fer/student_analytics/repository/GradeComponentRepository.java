package com.fer.student_analytics.repository;

import com.fer.student_analytics.model.GradeComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface GradeComponentRepository extends JpaRepository<GradeComponent, UUID> {
    List<GradeComponent> findByCourseId(UUID courseId);
    boolean existsByNameAndCourseId(String name, UUID courseId); // provjera duplikata unutar istog kolegija
}