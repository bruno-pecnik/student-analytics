package com.fer.student_analytics.repository;

import com.fer.student_analytics.model.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRecordRepository extends JpaRepository<StudentRecord, UUID> {
    List<StudentRecord> findByEnrollmentId(UUID enrollmentId);
    List<StudentRecord> findByComponentId(UUID componentId);
    boolean existsByEnrollmentIdAndComponentId(UUID enrollmentId, UUID componentId); // provjera duplikata - isti student ne može imati više zapisa za istu komponentu
}