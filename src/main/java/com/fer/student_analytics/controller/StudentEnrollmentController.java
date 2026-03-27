package com.fer.student_analytics.controller;

import com.fer.student_analytics.model.StudentEnrollment;
import com.fer.student_analytics.service.StudentEnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class StudentEnrollmentController {

    private final StudentEnrollmentService enrollmentService; 

    public StudentEnrollmentController(StudentEnrollmentService enrollmentService) { 
        this.enrollmentService = enrollmentService;
    }

    // GET /api/enrollments, dohvati sve upise
    @GetMapping
    public ResponseEntity<List<StudentEnrollment>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments()); // 200 OK
    }

    // GET /api/enrollments/by-student/{studentId}, dohvati upise po studentu
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<StudentEnrollment>> getEnrollmentsByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId)); // 200 OK
    }

    // GET /api/enrollments/by-group/{groupId}, dohvati upise po grupi
    @GetMapping("/by-group/{groupId}")
    public ResponseEntity<List<StudentEnrollment>> getEnrollmentsByGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByGroup(groupId)); // 200 OK
    }

    // GET /api/enrollments/{id}, dohvati upis po ID-u
    @GetMapping("/{id}")
    public ResponseEntity<StudentEnrollment> getEnrollmentById(@PathVariable UUID id) {
        Optional<StudentEnrollment> enrollment = enrollmentService.getEnrollmentById(id);
        if (enrollment.isPresent()) {
            return ResponseEntity.ok(enrollment.get()); // 200 OK
        }
        return ResponseEntity.notFound().build(); // 404 ako ne postoji
    }

    // POST /api/enrollments, upiši studenta u grupu
    @PostMapping
    public ResponseEntity<StudentEnrollment> enrollStudent(@RequestBody StudentEnrollment enrollment) {
        StudentEnrollment created = enrollmentService.enrollStudent(enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    // DELETE /api/enrollments/{id}, obršii upis
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeEnrollment(@PathVariable UUID id) {
        enrollmentService.removeEnrollment(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}