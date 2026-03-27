package com.fer.student_analytics.service;

import com.fer.student_analytics.exception.ResourceNotFoundException;
import com.fer.student_analytics.model.StudentEnrollment;
import com.fer.student_analytics.repository.StudentEnrollmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentEnrollmentService {

    private final StudentEnrollmentRepository enrollmentRepository; // referenca na bazu

    public StudentEnrollmentService(StudentEnrollmentRepository enrollmentRepository) { // konstruktor
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<StudentEnrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<StudentEnrollment> getEnrollmentsByStudent(UUID studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<StudentEnrollment> getEnrollmentsByGroup(UUID groupId) {
        return enrollmentRepository.findByGroupId(groupId);
    }

    public Optional<StudentEnrollment> getEnrollmentById(UUID id) {
        return enrollmentRepository.findById(id);
    }

    public StudentEnrollment enrollStudent(StudentEnrollment enrollment) { // prvo validiram, pa spremam
        validateEnrollment(enrollment);
        validateUniqueness(enrollment);
        return enrollmentRepository.save(enrollment);
    }

    public void removeEnrollment(UUID id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Upis sa ID: " + id + " nije pronađen.");
        }
        enrollmentRepository.deleteById(id);
    }

    private void validateEnrollment(StudentEnrollment enrollment) {
        // student mora biti pridružen
        if (enrollment.getStudent() == null || enrollment.getStudent().getId() == null) {
            throw new IllegalArgumentException("Upis mora imati pridruženog studenta.");
        }
        // grupa mora biti pridružena
        if (enrollment.getGroup() == null || enrollment.getGroup().getId() == null) {
            throw new IllegalArgumentException("Upis mora imati pridruženu grupu.");
        }
    }

    private void validateUniqueness(StudentEnrollment enrollment) {
        UUID studentId = enrollment.getStudent().getId();
        UUID groupId = enrollment.getGroup().getId();

        // isti student ne može biti upisan u istu grupu više puta
        if (enrollmentRepository.existsByStudentIdAndGroupId(studentId, groupId)) {
            throw new IllegalArgumentException("Student je već upisan u ovu grupu.");
        }
    }
}