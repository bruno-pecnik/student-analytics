package com.fer.student_analytics.repository;

import com.fer.student_analytics.model.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository 
public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {
}