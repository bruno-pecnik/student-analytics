package com.fer.student_analytics.service;

import com.fer.student_analytics.exception.ResourceNotFoundException;
import com.fer.student_analytics.model.CourseProfessor;
import com.fer.student_analytics.repository.CourseProfessorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseProfessorService {

    private final CourseProfessorRepository courseProfessorRepository; // referenca na bazu

    public CourseProfessorService(CourseProfessorRepository courseProfessorRepository) { // konstruktor
        this.courseProfessorRepository = courseProfessorRepository;
    }

    public List<CourseProfessor> getProfessorsByCourse(UUID courseId) {
        return courseProfessorRepository.findByCourseId(courseId);
    }

    public List<CourseProfessor> getCoursesByProfessor(UUID professorId) {
        return courseProfessorRepository.findByProfessorId(professorId);
    }

    public Optional<CourseProfessor> getById(UUID id) {
        return courseProfessorRepository.findById(id);
    }

    public CourseProfessor assignProfessor(CourseProfessor courseProfessor) { // prvo validiram, pa spremam
        validateAssignment(courseProfessor);
        validateUniqueness(courseProfessor);
        return courseProfessorRepository.save(courseProfessor);
    }

    public void removeProfessor(UUID id) {
        if (!courseProfessorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Veza profesor-kolegij sa ID: " + id + " nije pronađena.");
        }
        courseProfessorRepository.deleteById(id);
    }

    private void validateAssignment(CourseProfessor courseProfessor) {
        // kolegij mora biti pridružen
        if (courseProfessor.getCourse() == null || 
            courseProfessor.getCourse().getId() == null) {
            throw new IllegalArgumentException("Kolegij mora biti pridružen."); // provjerava postoji li kolegij
        }
        // profesor mora biti pridružen
        if (courseProfessor.getProfessor() == null || 
            courseProfessor.getProfessor().getId() == null) {
            throw new IllegalArgumentException("Profesor mora biti pridružen."); // provjerava postoji li profesor
        }
    }

    private void validateUniqueness(CourseProfessor courseProfessor) { // provjerava da ne postoji duplikat veze profesor-kolegij
        UUID courseId = courseProfessor.getCourse().getId();
        UUID professorId = courseProfessor.getProfessor().getId();

        // isti profesor ne može biti dodijeljen istom kolegiju više puta
        if (courseProfessorRepository.existsByCourseIdAndProfessorId(courseId, professorId)) {
            throw new IllegalArgumentException("Profesor je već dodijeljen ovom kolegiju.");
        }
    }
}