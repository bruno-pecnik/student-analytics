package com.fer.student_analytics.repository;

import com.fer.student_analytics.model.GradeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface GradeRuleRepository extends JpaRepository<GradeRule, UUID> {
    List<GradeRule> findByCourseId(UUID courseId);
    boolean existsByGradeAndCourseId(Integer grade, UUID courseId); // provjera duplikata iste ocjene unutar istog kolegija
}