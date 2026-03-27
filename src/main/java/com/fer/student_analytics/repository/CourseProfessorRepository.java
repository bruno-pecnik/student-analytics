package com.fer.student_analytics.repository;

import com.fer.student_analytics.model.CourseProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseProfessorRepository extends JpaRepository<CourseProfessor, UUID> {
    List<CourseProfessor> findByCourseId(UUID courseId);
    List<CourseProfessor> findByProfessorId(UUID professorId);
    boolean existsByCourseIdAndProfessorId(UUID courseId, UUID professorId); // Spring generira SQL iz imena,  provjera duplikata
}