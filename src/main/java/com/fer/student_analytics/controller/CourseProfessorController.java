package com.fer.student_analytics.controller;

import com.fer.student_analytics.model.CourseProfessor;
import com.fer.student_analytics.service.CourseProfessorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course-professors")
@CrossOrigin(origins = "*")
public class CourseProfessorController { // ovo je veza odnosno relacija pa nema standardnog CRUD-a

    private final CourseProfessorService courseProfessorService; 

    public CourseProfessorController(CourseProfessorService courseProfessorService) { 
        this.courseProfessorService = courseProfessorService;
    }

    // GET /api/course-professors/by-course/{courseId}, dohvati profesore po kolegiju
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<CourseProfessor>> getProfessorsByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseProfessorService.getProfessorsByCourse(courseId)); // 200 OK
    }

    // GET /api/course-professors/by-professor/{professorId}, dohvati kolegije po profesoru
    @GetMapping("/by-professor/{professorId}")
    public ResponseEntity<List<CourseProfessor>> getCoursesByProfessor(@PathVariable UUID professorId) {
        return ResponseEntity.ok(courseProfessorService.getCoursesByProfessor(professorId)); // 200 OK
    }

    // POST /api/course-professors, dodaj profesora na kolegij
    @PostMapping
    public ResponseEntity<CourseProfessor> assignProfessor(@RequestBody CourseProfessor courseProfessor) {
        CourseProfessor created = courseProfessorService.assignProfessor(courseProfessor);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    // DELETE /api/course-professors/{id}, ukloni profesora s kolegija
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeProfessor(@PathVariable UUID id) {courseProfessorService.removeProfessor(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}