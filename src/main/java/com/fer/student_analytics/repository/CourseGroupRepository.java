package com.fer.student_analytics.repository;

import com.fer.student_analytics.model.CourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseGroupRepository extends JpaRepository<CourseGroup, UUID> {
    List<CourseGroup> findByCourseId(UUID courseId);
}